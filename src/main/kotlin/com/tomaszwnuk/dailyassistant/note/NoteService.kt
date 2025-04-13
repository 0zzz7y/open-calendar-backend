package com.tomaszwnuk.dailyassistant.note

import com.tomaszwnuk.dailyassistant.calendar.Calendar
import com.tomaszwnuk.dailyassistant.calendar.CalendarRepository
import com.tomaszwnuk.dailyassistant.category.Category
import com.tomaszwnuk.dailyassistant.category.CategoryRepository
import com.tomaszwnuk.dailyassistant.domain.utility.info
import com.tomaszwnuk.dailyassistant.validation.findOrThrow
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class NoteService(
    private val _noteRepository: NoteRepository,
    private val _calendarRepository: CalendarRepository,
    private val _categoryRepository: CategoryRepository,
) {

    private var _timer: Long = 0

    @CacheEvict(cacheNames = ["calendarNotes"], key = "#dto.calendarId")
    fun create(dto: NoteDto): Note {
        info(this, "Creating $dto")
        _timer = System.currentTimeMillis()
        val calendar: Calendar = _calendarRepository.findOrThrow(id = dto.calendarId)
        val category: Category? = dto.categoryId?.let { _categoryRepository.findOrThrow(id = it) }
        val note = Note(
            name = dto.name,
            description = dto.description,
            calendar = calendar,
            category = category
        )

        val created: Note = _noteRepository.save(note)
        info(this, "Created $created in ${System.currentTimeMillis() - _timer} ms")

        return created
    }

    @Cacheable
    fun getById(id: UUID): Note {
        info(this, "Fetching note with id $id")
        _timer = System.currentTimeMillis()
        val note: Note = _noteRepository.findOrThrow(id)

        info(this, "Found $note in ${System.currentTimeMillis() - _timer} ms")
        return note
    }

    fun getAll(pageable: Pageable): Page<Note> {
        info(this, "Fetching all notes")
        val notes: Page<Note> = _noteRepository.findAll(pageable)

        info(this, "Found $notes")
        return notes
    }

    @Cacheable
    fun getAllByCalendarId(calendarId: UUID, pageable: Pageable): Page<Note> {
        info(this, "Fetching all notes for calendar with id $calendarId")
        _timer = System.currentTimeMillis()
        val notes: Page<Note> = _noteRepository.findAllByCalendarId(calendarId, pageable)

        info(this, "Found $notes in ${System.currentTimeMillis() - _timer} ms")
        return notes
    }

    fun filter(filter: NoteFilterDto, pageable: Pageable): Page<Note> {
        info(this, "Filtering notes with $filter")
        _timer = System.currentTimeMillis()
        val filtered: Page<Note> = _noteRepository.filter(
            name = filter.name,
            description = filter.description,
            calendarId = filter.calendarId,
            categoryId = filter.categoryId,
            pageable = pageable
        )

        info(this, "Found $filtered in ${System.currentTimeMillis() - _timer} ms")
        return filtered
    }

    @CacheEvict(cacheNames = ["calendarNotes"], key = "#dto.calendarId")
    fun update(id: UUID, dto: NoteDto): Note {
        info(this, "Updating $dto")
        _timer = System.currentTimeMillis()
        val existing: Note = getById(id)
        val calendar: Calendar = _calendarRepository.findOrThrow(id = dto.calendarId)
        val category: Category? = dto.categoryId?.let { _categoryRepository.findOrThrow(id = it) }
        val changed: Note = existing.copy(
            name = dto.name,
            description = dto.description,
            calendar = calendar,
            category = category
        )

        val updated: Note = _noteRepository.save(changed)
        info(this, "Updated $updated in ${System.currentTimeMillis() - _timer} ms")

        return updated
    }

    @CacheEvict(cacheNames = ["calendarNotes"], key = "#dto.calendarId")
    fun delete(id: UUID) {
        info(this, "Deleting note with id $id.")
        _timer = System.currentTimeMillis()
        val existing: Note = getById(id)

        _noteRepository.delete(existing)
        info(this, "Deleting note $existing in ${System.currentTimeMillis() - _timer} ms")
    }

}
