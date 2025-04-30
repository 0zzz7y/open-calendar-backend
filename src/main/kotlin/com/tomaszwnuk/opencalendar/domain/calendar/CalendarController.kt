/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.calendar

import com.tomaszwnuk.opencalendar.domain.event.EventDto
import com.tomaszwnuk.opencalendar.domain.event.EventService
import com.tomaszwnuk.opencalendar.domain.mapper.ItemTypeMapper.toMapWithType
import com.tomaszwnuk.opencalendar.domain.mapper.PageMapper.toPage
import com.tomaszwnuk.opencalendar.domain.note.NoteDto
import com.tomaszwnuk.opencalendar.domain.note.NoteService
import com.tomaszwnuk.opencalendar.domain.task.TaskDto
import com.tomaszwnuk.opencalendar.domain.task.TaskService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * REST controller for managing calendars and their related entities.
 *
 * @property _calendarService Service for managing calendar entities.
 * @property _eventService Service for managing event entities.
 * @property _taskService Service for managing task entities.
 * @property _noteService Service for managing note entities.
 */
@Suppress("unused")
@RestController
@RequestMapping("/calendars")
class CalendarController(
    private val _calendarService: CalendarService,
    private val _eventService: EventService,
    private val _taskService: TaskService,
    private val _noteService: NoteService
) {

    /**
     * Creates a new calendar.
     *
     * @param dto The calendar data transfer object containing the details of the calendar to create.
     *
     * @return A `ResponseEntity` containing the created calendar.
     */
    @PostMapping
    fun create(@Valid @RequestBody(required = true) dto: CalendarDto): ResponseEntity<CalendarDto> {
        val created: CalendarDto = _calendarService.create(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    /**
     * Retrieves all calendars with pagination.
     *
     * @param pageable The pagination and sorting information.
     *
     * @return A `ResponseEntity` containing a paginated list of calendars.
     */
    @GetMapping
    fun getAll(
        @PageableDefault(
            size = 10,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable
    ): ResponseEntity<Page<CalendarDto>> {
        val calendars: List<CalendarDto> = _calendarService.getAll()
        return ResponseEntity.ok(calendars.toPage(pageable))
    }

    /**
     * Retrieves a calendar by its ID.
     *
     * @param id The unique identifier of the calendar.
     *
     * @return A `ResponseEntity` containing the calendar details.
     */
    @GetMapping("/{id}")
    fun getById(@PathVariable(name = "id", required = true) id: UUID): ResponseEntity<CalendarDto> {
        val calendar: CalendarDto = _calendarService.getById(id)
        return ResponseEntity.ok(calendar)
    }

    /**
     * Retrieves events associated with a specific calendar.
     *
     * @param id The unique identifier of the calendar.
     * @param pageable The pagination and sorting information.
     *
     * @return A `ResponseEntity` containing a paginated list of events.
     */
    @GetMapping("/{id}/events")
    fun getEvents(
        @PathVariable(name = "id", required = true) id: UUID,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<EventDto>> {
        val events: List<EventDto> = _eventService.getAllByCalendarId(id)
        return ResponseEntity.ok(events.toPage(pageable))
    }

    /**
     * Retrieves tasks associated with a specific calendar.
     *
     * @param id The unique identifier of the calendar.
     * @param pageable The pagination and sorting information.
     *
     * @return A `ResponseEntity` containing a paginated list of tasks.
     */
    @GetMapping("/{id}/tasks")
    fun getTasks(
        @PathVariable(name = "id", required = true) id: UUID,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<TaskDto>> {
        val tasks: List<TaskDto> = _taskService.getAllByCalendarId(id)
        return ResponseEntity.ok(tasks.toPage(pageable))
    }

    /**
     * Retrieves notes associated with a specific calendar.
     *
     * @param id The unique identifier of the calendar.
     * @param pageable The pagination and sorting information.
     *
     * @return A `ResponseEntity` containing a paginated list of notes.
     */
    @GetMapping("/{id}/notes")
    fun getNotes(
        @PathVariable(name = "id", required = true) id: UUID,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<NoteDto>> {
        val notes: List<NoteDto> = _noteService.getAllByCalendarId(id)
        return ResponseEntity.ok(notes.toPage(pageable))
    }

    /**
     * Retrieves all items (events, tasks, notes) associated with a specific calendar.
     *
     * @param id The unique identifier of the calendar.
     * @param pageable The pagination and sorting information.
     *
     * @return A `ResponseEntity` containing a list of items with their types.
     */
    @GetMapping("/{id}/items")
    fun getAllItems(
        @PathVariable(name = "id", required = true) id: UUID,
        @PageableDefault(
            size = 10,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable
    ): ResponseEntity<List<Map<String, Any>>> {
        val events: Page<Map<String, Any>> = _eventService.getAllByCalendarId(id).map {
            it.toMapWithType("event")
        }.toPage(pageable)
        val tasks: Page<Map<String, Any>> = _taskService.getAllByCalendarId(id).map {
            it.toMapWithType("task")
        }.toPage(pageable)
        val notes: Page<Map<String, Any>> = _noteService.getAllByCalendarId(id).map {
            it.toMapWithType("note")
        }.toPage(pageable)

        val items = events + tasks + notes
        return ResponseEntity.ok(items)
    }

    /**
     * Filters calendars based on title and emoji.
     *
     * @param title The title to filter by (optional).
     * @param emoji The emoji to filter by (optional).
     * @param pageable The pagination and sorting information.
     *
     * @return A `ResponseEntity` containing a paginated list of filtered calendars.
     */
    @GetMapping("/filter")
    fun filter(
        @RequestParam(name = "title", required = false) title: String?,
        @RequestParam(name = "emoji", required = false) emoji: String?,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<CalendarDto>> {
        val filter = CalendarFilterDto(
            title = title,
            emoji = emoji
        )
        val calendars: List<CalendarDto> = _calendarService.filter(filter)
        return ResponseEntity.ok(calendars.toPage(pageable))
    }

    /**
     * Updates an existing calendar.
     *
     * @param id The unique identifier of the calendar to update.
     * @param dto The calendar data transfer object containing the updated details.
     *
     * @return A `ResponseEntity` containing the updated calendar.
     */
    @PutMapping("/{id}")
    fun update(
        @PathVariable(name = "id", required = true) id: UUID,
        @Valid @RequestBody dto: CalendarDto
    ): ResponseEntity<CalendarDto> {
        val updated: CalendarDto = _calendarService.update(id, dto)
        return ResponseEntity.ok(updated)
    }

    /**
     * Deletes a calendar and its associated entities (events, tasks, notes).
     *
     * @param id The unique identifier of the calendar to delete.
     *
     * @return A `ResponseEntity` with no content.
     */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable(name = "id", required = true) id: UUID): ResponseEntity<Void> {
        _eventService.deleteAllByCalendarId(id)
        _taskService.deleteAllByCalendarId(id)
        _noteService.deleteByCalendarId(id)
        _calendarService.delete(id)
        return ResponseEntity.noContent().build()
    }

}
