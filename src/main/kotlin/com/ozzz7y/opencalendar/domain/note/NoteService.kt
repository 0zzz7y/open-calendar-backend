package com.ozzz7y.opencalendar.domain.note

import com.ozzz7y.opencalendar.domain.calendar.Calendar
import com.ozzz7y.opencalendar.domain.calendar.CalendarRepository
import com.ozzz7y.opencalendar.domain.category.Category
import com.ozzz7y.opencalendar.domain.category.CategoryRepository
import com.ozzz7y.opencalendar.domain.user.UserService
import com.ozzz7y.opencalendar.utility.logger.info
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import java.util.*

/**
 * The service for note operations.
 */
@Service
class NoteService(

    /**
     * The repository for managing note data.
     */
    private val _noteRepository: NoteRepository,

    /**
     * The repository for managing calendar data.
     */
    private val _calendarRepository: CalendarRepository,

    /**
     * The repository for managing category data.
     */
    private val _categoryRepository: CategoryRepository,

    /**
     * The service for user operations.
     */
    private val _userService: UserService

) {

    /**
     * The timer for measuring the duration of operations.
     */
    private var _timer: Long = 0

    /**
     * Creates a new note.
     *
     * @param dto The data transfer object containing note details
     *
     * @return The created note as a data transfer object
     *
     * @throws IllegalArgumentException If the calendar does not exist for the user
     */
    @Caching(
        evict = [
            CacheEvict(cacheNames = ["allNotes"], allEntries = true),
            CacheEvict(cacheNames = ["calendarNotes"], allEntries = true),
            CacheEvict(cacheNames = ["categoryNotes"], allEntries = true)
        ]
    )
    fun create(dto: NoteDto): NoteDto {
        info(source = this, message = "Creating $dto")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val calendar: Optional<Calendar> = _calendarRepository.findByIdAndUserId(id = dto.calendarId, userId = userId)
        if (calendar.isEmpty) {
            throw IllegalArgumentException("Calendar with id ${dto.calendarId} not found for user $userId")
        }

        val category: Optional<Category>? = dto.categoryId?.let {
            _categoryRepository.findByIdAndUserId(id = it, userId = userId)
        }

        val note = Note(
            name = dto.name,
            description = dto.description,
            calendar = calendar.get(),
            category = category?.get()
        )

        val created: Note = _noteRepository.save(note)
        info(source = this, message = "Created $created in ${System.currentTimeMillis() - _timer} ms")
        return created.toDto()
    }

    /**
     * Retrieves a note by its unique identifier.
     *
     * @param id The unique identifier of the note
     *
     * @return The note as a data transfer object
     *
     * @throws NoSuchElementException If the note does not exist for the user
     */
    @Cacheable(cacheNames = ["noteById"], key = "#id", condition = "#id != null")
    fun getById(id: UUID): NoteDto {
        info(source = this, message = "Fetching note with id $id")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val note: Optional<Note> = _noteRepository.findByIdAndCalendarUserId(id = id, userId = userId)
        if (note.isEmpty) {
            throw NoSuchElementException("Note with id $id not found for user $userId")
        }

        info(source = this, message = "Found $note in ${System.currentTimeMillis() - _timer} ms")
        return note.get().toDto()
    }

    /**
     * Retrieves all notes associated with a specific calendar.
     *
     * @param calendarId The unique identifier of the calendar
     *
     * @return A list of notes as data transfer objects
     */
    @Cacheable(cacheNames = ["calendarNotes"], key = "#calendarId", condition = "#calendarId != null")
    fun getAllByCalendarId(calendarId: UUID): List<NoteDto> {
        info(source = this, message = "Fetching all notes for calendar with id $calendarId")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val notes: List<Note> = _noteRepository.findAllByCalendarIdAndUserId(
            calendarId = calendarId,
            userId = userId
        )

        info(source = this, message = "Found $notes in ${System.currentTimeMillis() - _timer} ms")
        return notes.map { it.toDto() }
    }

    /**
     * Retrieves all notes associated with a specific category.
     *
     * @param categoryId The unique identifier of the category
     *
     * @return A list of notes as data transfer objects
     */
    @Cacheable(cacheNames = ["categoryNotes"], key = "#categoryId", condition = "#categoryId != null")
    fun getAllByCategoryId(categoryId: UUID): List<NoteDto> {
        info(source = this, message = "Fetching all notes for category with id $categoryId")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val notes: List<Note> = _noteRepository.findAllByCategoryIdAndUserId(
            categoryId = categoryId,
            userId = userId
        )

        info(source = this, message = "Found $notes in ${System.currentTimeMillis() - _timer} ms")
        return notes.map { it.toDto() }
    }

    /**
     * Retrieves all notes for the current user.
     *
     * @return A list of all notes as data transfer objects
     */
    @Cacheable(cacheNames = ["allNotes"], condition = "#result != null")
    fun getAll(): List<NoteDto> {
        info(source = this, message = "Fetching all notes")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val notes: List<Note> = _noteRepository.findAllByCalendarUserId(userId = userId)

        info(source = this, message = "Found $notes in ${System.currentTimeMillis() - _timer} ms")
        return notes.map { it.toDto() }
    }

    /**
     * Filters notes based on the provided criteria.
     *
     * @param filter The filter criteria as a data transfer object
     *
     * @return A list of notes that match the filter criteria
     */
    fun filter(filter: NoteFilterDto): List<NoteDto> {
        info(source = this, message = "Filtering notes with $filter")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val filtered: List<Note> = _noteRepository.filter(
            userId = userId,
            name = filter.name,
            description = filter.description,
            calendarId = filter.calendarId,
            categoryId = filter.categoryId
        )

        info(source = this, message = "Found $filtered in ${System.currentTimeMillis() - _timer} ms")
        return filtered.map { it.toDto() }
    }

    /**
     * Updates an existing note.
     *
     * @param id The unique identifier of the note to update
     * @param dto The data transfer object containing the updated details of the note
     *
     * @return The updated note as a data transfer object
     *
     * @throws NoSuchElementException if the note does not exist for the user
     * @throws IllegalArgumentException If the calendar does not exist for the user
     */
    @Caching(
        evict = [
            CacheEvict(cacheNames = ["noteById"], key = "#id"),
            CacheEvict(cacheNames = ["allNotes"], allEntries = true),
            CacheEvict(cacheNames = ["calendarNotes"], allEntries = true),
            CacheEvict(cacheNames = ["categoryNotes"], allEntries = true)
        ]
    )
    fun update(id: UUID, dto: NoteDto): NoteDto {
        info(source = this, message = "Updating $dto")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val existing: Optional<Note> = _noteRepository.findByIdAndCalendarUserId(id = id, userId = userId)
        if (existing.isEmpty) {
            throw NoSuchElementException("Event with id $id not found for user $userId")
        }

        val calendar: Optional<Calendar> = _calendarRepository.findByIdAndUserId(id = dto.calendarId, userId = userId)
        if (calendar.isEmpty) {
            throw IllegalArgumentException("Calendar with id ${dto.calendarId} not found for user $userId")
        }

        val category: Optional<Category>? = dto.categoryId?.let {
            _categoryRepository.findByIdAndUserId(id = it, userId = userId)
        }

        val updated: Note = existing.get().copy(
            name = dto.name,
            description = dto.description,
            calendar = calendar.get(),
            category = category?.get()
        )

        val saved: Note = _noteRepository.save(updated)
        info(source = this, message = "Updated $updated in ${System.currentTimeMillis() - _timer} ms")
        return saved.toDto()
    }

    /**
     * Deletes a note by its unique identifier.
     *
     * @param id The unique identifier of the note to delete
     *
     * @throws NoSuchElementException If the note does not exist for the user
     */
    @Caching(
        evict = [
            CacheEvict(cacheNames = ["noteById"], key = "#id"),
            CacheEvict(cacheNames = ["allNotes"], allEntries = true),
            CacheEvict(cacheNames = ["calendarNotes"], allEntries = true),
            CacheEvict(cacheNames = ["categoryNotes"], allEntries = true)
        ]
    )
    fun delete(id: UUID) {
        info(source = this, message = "Deleting note with id $id.")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val existing: Optional<Note> = _noteRepository.findByIdAndCalendarUserId(id = id, userId = userId)
        if (existing.isEmpty) {
            throw NoSuchElementException("Event with id $id not found for user $userId")
        }

        _noteRepository.delete(existing.get())
        info(source = this, message = "Deleted note $existing in ${System.currentTimeMillis() - _timer} ms")
    }

    /**
     * Deletes all notes associated with a specific calendar.
     *
     * @param calendarId The unique identifier of the calendar
     */
    @Caching(
        evict = [
            CacheEvict(cacheNames = ["noteById"], key = "#id", condition = "#id != null"),
            CacheEvict(cacheNames = ["allNotes"], allEntries = true),
            CacheEvict(cacheNames = ["calendarNotes"], allEntries = true),
            CacheEvict(cacheNames = ["categoryNotes"], allEntries = true)
        ]
    )
    fun deleteByCalendarId(calendarId: UUID) {
        info(source = this, message = "Deleting all notes for calendar with id $calendarId.")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val notes: List<Note> = _noteRepository.findAllByCalendarIdAndUserId(
            calendarId = calendarId,
            userId = userId
        )

        _noteRepository.deleteAll(notes)
        info(
            source = this,
            message = "Deleted all notes for calendar with id $calendarId in ${System.currentTimeMillis() - _timer} ms"
        )
    }

    /**
     * Removes the category from all notes associated with a specific category.
     *
     * @param categoryId The unique identifier of the category to remove
     */
    @Caching(
        evict = [
            CacheEvict(cacheNames = ["noteById"], key = "#id", condition = "#id != null"),
            CacheEvict(cacheNames = ["allNotes"], allEntries = true),
            CacheEvict(cacheNames = ["calendarNotes"], allEntries = true),
            CacheEvict(cacheNames = ["categoryNotes"], allEntries = true)
        ]
    )
    fun removeCategoryByCategoryId(categoryId: UUID) {
        info(source = this, message = "Updating all notes for category with id $categoryId.")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val notes: List<Note> = _noteRepository.findAllByCategoryIdAndUserId(
            categoryId = categoryId,
            userId = userId
        )
        notes.forEach { note ->
            val withoutCategory = note.copy(category = null)
            _noteRepository.save(withoutCategory)
        }

        info(
            source = this,
            message = "Updated category to null for all notes in ${System.currentTimeMillis() - _timer} ms"
        )
    }

}
