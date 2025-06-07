/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.note

import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.calendar.CalendarRepository
import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.category.CategoryRepository
import com.tomaszwnuk.opencalendar.utility.logger.info
import com.tomaszwnuk.opencalendar.utility.validation.repository.findOrThrow
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import java.util.*

@Service
class NoteService(
    private val _noteRepository: NoteRepository,
    private val _calendarRepository: CalendarRepository,
    private val _categoryRepository: CategoryRepository
) {

    private var _timer: Long = 0

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
            name = dto.name,
            description = dto.description,
            calendar = calendar,
            category = category
        )

        val created: Note = _noteRepository.save(note)

        info(source = this, message = "Created $created in ${System.currentTimeMillis() - _timer} ms")
        return created.toDto()
    }

    @Cacheable(cacheNames = ["noteById"], key = "#id", condition = "#id != null")
    fun getById(id: UUID): NoteDto {
        info(source = this, message = "Fetching note with id $id")
        _timer = System.currentTimeMillis()

        val note: Note = _noteRepository.findOrThrow(id = id)

        info(source = this, message = "Found $note in ${System.currentTimeMillis() - _timer} ms")
        return note.toDto()
    }

    @Cacheable(cacheNames = ["calendarNotes"], key = "#calendarId", condition = "#calendarId != null")
    fun getAllByCalendarId(calendarId: UUID): List<NoteDto> {
        info(source = this, message = "Fetching all notes for calendar with id $calendarId")
        _timer = System.currentTimeMillis()

        val notes: List<Note> = _noteRepository.findAllByCalendarId(calendarId = calendarId)

        info(source = this, message = "Found $notes in ${System.currentTimeMillis() - _timer} ms")
        return notes.map { it.toDto() }
    }

    @Cacheable(cacheNames = ["categoryNotes"], key = "#categoryId", condition = "#categoryId != null")
    fun getAllByCategoryId(categoryId: UUID): List<NoteDto> {
        info(source = this, message = "Fetching all notes for category with id $categoryId")
        _timer = System.currentTimeMillis()

        val notes: List<Note> = _noteRepository.findAllByCategoryId(categoryId = categoryId)

        info(source = this, message = "Found $notes in ${System.currentTimeMillis() - _timer} ms")
        return notes.map { it.toDto() }
    }

    @Cacheable(cacheNames = ["allNotes"], condition = "#result != null")
    fun getAll(): List<NoteDto> {
        info(source = this, message = "Fetching all notes")
        _timer = System.currentTimeMillis()

        val notes: List<Note> = _noteRepository.findAll()

        info(source = this, message = "Found $notes in ${System.currentTimeMillis() - _timer} ms")
        return notes.map { it.toDto() }
    }

    fun filter(filter: NoteFilterDto): List<NoteDto> {
        info(source = this, message = "Filtering notes with $filter")
        _timer = System.currentTimeMillis()

        val filtered: List<Note> = _noteRepository.filter(
            name = filter.name,
            description = filter.description,
            calendarId = filter.calendarId,
            categoryId = filter.categoryId
        )

        info(source = this, message = "Found $filtered in ${System.currentTimeMillis() - _timer} ms")
        return filtered.map { it.toDto() }
    }

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
            name = dto.name,
            description = dto.description,
            calendar = calendar,
            category = category
        )

        val updated: Note = _noteRepository.save(changed)

        info(source = this, message = "Updated $updated in ${System.currentTimeMillis() - _timer} ms")
        return updated.toDto()
    }

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
