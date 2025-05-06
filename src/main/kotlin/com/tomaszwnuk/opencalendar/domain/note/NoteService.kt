/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.note

import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.calendar.CalendarRepository
import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.category.CategoryRepository
import com.tomaszwnuk.opencalendar.utility.logger.info
import com.tomaszwnuk.opencalendar.utility.validation.findOrThrow
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import java.util.*

/**
 * Service class for managing notes.
 * Provides methods for creating, retrieving, updating, and deleting notes.
 *
 * @property _noteRepository Repository for managing Note entities.
 * @property _calendarRepository Repository for managing Calendar entities.
 * @property _categoryRepository Repository for managing Category entities.
 */
@Service
class NoteService(
    private val _noteRepository: NoteRepository,
    private val _calendarRepository: CalendarRepository,
    private val _categoryRepository: CategoryRepository
) {

    /**
     * Timer used for logging execution time of operations.
     */
    private var _timer: Long = 0

    /**
     * Creates a new note and evicts related cache entries.
     *
     * @param dto The data transfer object containing note details.
     *
     * @return The created note as a DTO.
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

        val calendar: Calendar = _calendarRepository.findOrThrow(id = dto.calendarId)
        val category: Category? = dto.categoryId?.let { _categoryRepository.findOrThrow(id = it) }
        val note = Note(
            title = dto.title,
            description = dto.description,
            calendar = calendar,
            category = category
        )

        val created: Note = _noteRepository.save(note)

        info(source = this, message = "Created $created in ${System.currentTimeMillis() - _timer} ms")
        return created.toDto()
    }

    /**
     * Retrieves a note by its unique identifier and caches the result.
     *
     * @param id The UUID of the note to retrieve.
     *
     * @return The retrieved note as a DTO.
     */
    @Cacheable(cacheNames = ["noteById"], key = "#id", condition = "#id != null")
    fun getById(id: UUID): NoteDto {
        info(source = this, message = "Fetching note with id $id")
        _timer = System.currentTimeMillis()

        val note: Note = _noteRepository.findOrThrow(id = id)

        info(source = this, message = "Found $note in ${System.currentTimeMillis() - _timer} ms")
        return note.toDto()
    }

    /**
     * Retrieves all notes associated with a specific calendar and caches the result.
     *
     * @param calendarId The UUID of the calendar.
     *
     * @return A list of notes as DTOs.
     */
    @Cacheable(cacheNames = ["calendarNotes"], key = "#calendarId", condition = "#calendarId != null")
    fun getAllByCalendarId(calendarId: UUID): List<NoteDto> {
        info(source = this, message = "Fetching all notes for calendar with id $calendarId")
        _timer = System.currentTimeMillis()

        val notes: List<Note> = _noteRepository.findAllByCalendarId(calendarId = calendarId)

        info(source = this, message = "Found $notes in ${System.currentTimeMillis() - _timer} ms")
        return notes.map { it.toDto() }
    }

    /**
     * Retrieves all notes associated with a specific category and caches the result.
     *
     * @param categoryId The UUID of the category.
     *
     * @return A list of notes as DTOs.
     */
    @Cacheable(cacheNames = ["categoryNotes"], key = "#categoryId", condition = "#categoryId != null")
    fun getAllByCategoryId(categoryId: UUID): List<NoteDto> {
        info(source = this, message = "Fetching all notes for category with id $categoryId")
        _timer = System.currentTimeMillis()

        val notes: List<Note> = _noteRepository.findAllByCategoryId(categoryId = categoryId)

        info(source = this, message = "Found $notes in ${System.currentTimeMillis() - _timer} ms")
        return notes.map { it.toDto() }
    }

    /**
     * Retrieves all notes and caches the result.
     *
     * @return A list of all notes as DTOs.
     */
    @Cacheable(cacheNames = ["allNotes"], condition = "#result != null")
    fun getAll(): List<NoteDto> {
        info(source = this, message = "Fetching all notes")
        _timer = System.currentTimeMillis()

        val notes: List<Note> = _noteRepository.findAll()

        info(source = this, message = "Found $notes in ${System.currentTimeMillis() - _timer} ms")
        return notes.map { it.toDto() }
    }

    /**
     * Filters notes based on the provided criteria.
     *
     * @param filter The filter criteria as a DTO.
     *
     * @return A list of filtered notes as DTOs.
     */
    fun filter(filter: NoteFilterDto): List<NoteDto> {
        info(source = this, message = "Filtering notes with $filter")
        _timer = System.currentTimeMillis()

        val filtered: List<Note> = _noteRepository.filter(
            title = filter.title,
            description = filter.description,
            calendarId = filter.calendarId,
            categoryId = filter.categoryId
        )

        info(source = this, message = "Found $filtered in ${System.currentTimeMillis() - _timer} ms")
        return filtered.map { it.toDto() }
    }

    /**
     * Updates an existing note and evicts related cache entries.
     *
     * @param id The UUID of the note to update.
     * @param dto The data transfer object containing updated note details.
     *
     * @return The updated note as a DTO.
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

        val existing: Note = _noteRepository.findOrThrow(id = id)
        val calendar: Calendar = _calendarRepository.findOrThrow(id = dto.calendarId)
        val category: Category? = dto.categoryId?.let { _categoryRepository.findOrThrow(id = it) }
        val changed: Note = existing.copy(
            title = dto.title,
            description = dto.description,
            calendar = calendar,
            category = category
        )

        val updated: Note = _noteRepository.save(changed)

        info(source = this, message = "Updated $updated in ${System.currentTimeMillis() - _timer} ms")
        return updated.toDto()
    }

    /**
     * Deletes a note by its unique identifier and evicts related cache entries.
     *
     * @param id The UUID of the note to delete.
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

        val existing: Note = _noteRepository.findOrThrow(id = id)

        _noteRepository.delete(existing)
        info(source = this, message = "Deleted note $existing in ${System.currentTimeMillis() - _timer} ms")
    }

    /**
     * Deletes all notes associated with a specific calendar and evicts related cache entries.
     *
     * @param calendarId The UUID of the calendar.
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

        val notes: List<Note> = _noteRepository.findAllByCalendarId(calendarId = calendarId)

        _noteRepository.deleteAll(notes)
        info(
            source = this,
            message = "Deleted all notes for calendar with id $calendarId in ${System.currentTimeMillis() - _timer} ms"
        )
    }

    /**
     * Removes the category association from all notes of a specific category and evicts related cache entries.
     *
     * @param categoryId The UUID of the category.
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

        val notes: List<Note> = _noteRepository.findAllByCategoryId(categoryId = categoryId)
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
