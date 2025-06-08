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

@Suppress("unused")
@RestController
@RequestMapping("/calendars")
class CalendarController(
    private val _calendarService: CalendarService,
    private val _eventService: EventService,
    private val _taskService: TaskService,
    private val _noteService: NoteService
) {

    @PostMapping
    fun create(@Valid @RequestBody(required = true) dto: CalendarDto): ResponseEntity<CalendarDto> {
        val created: CalendarDto = _calendarService.create(dto = dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @GetMapping
    fun getAll(
        @PageableDefault(
            size = 10,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable
    ): ResponseEntity<Page<CalendarDto>> {
        val calendars: List<CalendarDto> = _calendarService.getAll()
        return ResponseEntity.ok(calendars.toPage(pageable = pageable))
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable(name = "id", required = true) id: UUID): ResponseEntity<CalendarDto> {
        val calendar: CalendarDto = _calendarService.getById(id = id)
        return ResponseEntity.ok(calendar)
    }

    @GetMapping("/{id}/events")
    fun getEvents(
        @PathVariable(name = "id", required = true) id: UUID,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<EventDto>> {
        val events: List<EventDto> = _eventService.getAllByCalendarId(calendarId = id)
        return ResponseEntity.ok(events.toPage(pageable = pageable))
    }

    @GetMapping("/{id}/tasks")
    fun getTasks(
        @PathVariable(name = "id", required = true) id: UUID,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<TaskDto>> {
        val tasks: List<TaskDto> = _taskService.getAllByCalendarId(calendarId = id)
        return ResponseEntity.ok(tasks.toPage(pageable = pageable))
    }

    @GetMapping("/{id}/notes")
    fun getNotes(
        @PathVariable(name = "id", required = true) id: UUID,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<NoteDto>> {
        val notes: List<NoteDto> = _noteService.getAllByCalendarId(calendarId = id)
        return ResponseEntity.ok(notes.toPage(pageable = pageable))
    }

    @GetMapping("/{id}/items")
    fun getAllItems(
        @PathVariable(name = "id", required = true) id: UUID,
        @PageableDefault(
            size = 10,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable
    ): ResponseEntity<List<Map<String, Any>>> {
        val events: Page<Map<String, Any>> = _eventService.getAllByCalendarId(calendarId = id).map {
            it.toMapWithType(type = "event")
        }.toPage(pageable)
        val tasks: Page<Map<String, Any>> = _taskService.getAllByCalendarId(calendarId = id).map {
            it.toMapWithType(type = "task")
        }.toPage(pageable)
        val notes: Page<Map<String, Any>> = _noteService.getAllByCalendarId(calendarId = id).map {
            it.toMapWithType(type = "note")
        }.toPage(pageable)

        val items = events + tasks + notes
        return ResponseEntity.ok(items)
    }

    @GetMapping("/filter")
    fun filter(
        @RequestParam(name = "name", required = false) name: String?,
        @RequestParam(name = "emoji", required = false) emoji: String?,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<CalendarDto>> {
        val filter = CalendarFilterDto(
            name = name,
            emoji = emoji
        )
        val calendars: List<CalendarDto> = _calendarService.filter(filter = filter)
        return ResponseEntity.ok(calendars.toPage(pageable = pageable))
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable(name = "id", required = true) id: UUID,
        @Valid @RequestBody dto: CalendarDto
    ): ResponseEntity<CalendarDto> {
        val updated: CalendarDto = _calendarService.update(id = id, dto = dto)
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable(name = "id", required = true) id: UUID): ResponseEntity<Void> {
        _eventService.deleteAllByCalendarId(calendarId = id)
        _taskService.deleteAllByCalendarId(calendarId = id)
        _noteService.deleteByCalendarId(calendarId = id)
        _calendarService.delete(id)
        return ResponseEntity.noContent().build()
    }

}
