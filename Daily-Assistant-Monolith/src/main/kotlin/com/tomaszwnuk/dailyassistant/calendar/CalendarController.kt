package com.tomaszwnuk.dailyassistant.calendar

import com.tomaszwnuk.dailyassistant.event.EventDto
import com.tomaszwnuk.dailyassistant.event.EventRepository
import com.tomaszwnuk.dailyassistant.task.TaskDto
import com.tomaszwnuk.dailyassistant.task.TaskRepository
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/calendars")
class CalendarController(
    private val _calendarService: CalendarService,
    private val _eventRepository: EventRepository,
    private val _taskRepository: TaskRepository
) {

    @GetMapping
    fun getAll(): ResponseEntity<List<CalendarDto>> {
        val calendars: List<CalendarDto> = _calendarService.getAll().map { it.toDto() }
        return ResponseEntity.ok(calendars)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<CalendarDto> {
        val calendar: CalendarDto = _calendarService.getById(id).toDto()
        return ResponseEntity.ok(calendar)
    }

    @PostMapping
    fun create(@Valid @RequestBody dto: CalendarDto): ResponseEntity<CalendarDto> {
        val created: CalendarDto = _calendarService.create(dto).toDto()
        return ResponseEntity.status(201).body(created)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @Valid @RequestBody dto: CalendarDto): ResponseEntity<CalendarDto> {
        val updated: CalendarDto = _calendarService.update(id, dto).toDto()
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        _calendarService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}/events")
    fun getEvents(@PathVariable id: UUID): ResponseEntity<List<EventDto>> {
        val events: List<EventDto> = _eventRepository.findAllByCalendarId(id).map { it.toDto() }
        return ResponseEntity.ok(events)
    }

    @GetMapping("/{id}/tasks")
    fun getTasks(@PathVariable id: UUID): ResponseEntity<List<TaskDto>> {
        val tasks: List<TaskDto> = _taskRepository.findAllByCalendarId(id).map { it.toDto() }
        return ResponseEntity.ok(tasks)
    }

}
