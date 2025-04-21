package com.tomaszwnuk.dailyassistant.calendar

import com.tomaszwnuk.dailyassistant.domain.utility.ItemTypeMapper.toMapWithType
import com.tomaszwnuk.dailyassistant.event.EventDto
import com.tomaszwnuk.dailyassistant.event.EventService
import com.tomaszwnuk.dailyassistant.note.NoteDto
import com.tomaszwnuk.dailyassistant.note.NoteService
import com.tomaszwnuk.dailyassistant.task.TaskDto
import com.tomaszwnuk.dailyassistant.task.TaskService
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
    fun create(@Valid @RequestBody dto: CalendarDto): ResponseEntity<CalendarDto> {
        val created: CalendarDto = _calendarService.create(dto).toDto()
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
        val calendars: Page<CalendarDto> = _calendarService.getAll(pageable).map { it.toDto() }
        return ResponseEntity.ok(calendars)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<CalendarDto> {
        val calendar: CalendarDto = _calendarService.getById(id).toDto()
        return ResponseEntity.ok(calendar)
    }

    @GetMapping("/{id}/events")
    fun getEvents(
        @PathVariable id: UUID,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<EventDto>> {
        val events: Page<EventDto> = _eventService.getAllByCalendarId(id, pageable).map { it.toDto() }
        return ResponseEntity.ok(events)
    }

    @GetMapping("/{id}/tasks")
    fun getTasks(
        @PathVariable id: UUID,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<TaskDto>> {
        val tasks: Page<TaskDto> = _taskService.getAllByCalendarId(id, pageable).map { it.toDto() }
        return ResponseEntity.ok(tasks)
    }

    @GetMapping("/{id}/notes")
    fun getNotes(
        @PathVariable id: UUID,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<NoteDto>> {
        val notes: Page<NoteDto> = _noteService.getAllByCalendarId(id, pageable).map { it.toDto() }
        return ResponseEntity.ok(notes)
    }

    @GetMapping("/{id}/items")
    fun getAllItems(
        @PathVariable id: UUID,
        @PageableDefault(
            size = 10,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable
    ): ResponseEntity<List<Map<String, Any>>> {
        val events = _eventService.getAllByCalendarId(id, pageable).map {
            it.toDto().toMapWithType("event")
        }
        val tasks = _taskService.getAllByCalendarId(id, pageable).map {
            it.toDto().toMapWithType("task")
        }
        val notes = _noteService.getAllByCalendarId(id, pageable).map {
            it.toDto().toMapWithType("note")
        }

        val items = events + tasks + notes
        return ResponseEntity.ok(items)
    }

    @GetMapping("/filter")
    fun filter(
        @RequestParam(required = false) name: String?,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<CalendarDto>> {
        val filter = CalendarFilterDto(
            name = name
        )
        val calendars: Page<CalendarDto> = _calendarService.filter(filter, pageable).map { it.toDto() }
        return ResponseEntity.ok(calendars)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @Valid @RequestBody dto: CalendarDto): ResponseEntity<CalendarDto> {
        val updated: CalendarDto = _calendarService.update(id, dto).toDto()
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        _eventService.deleteAllByCalendarId(id)
        _taskService.deleteAllByCalendarId(id)
        _noteService.deleteAllByCalendarId(id)
        _calendarService.delete(id)
        return ResponseEntity.noContent().build()
    }

}
