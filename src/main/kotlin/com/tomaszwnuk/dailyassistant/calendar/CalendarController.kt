package com.tomaszwnuk.dailyassistant.calendar

import com.tomaszwnuk.dailyassistant.event.EventDto
import com.tomaszwnuk.dailyassistant.event.EventRepository
import com.tomaszwnuk.dailyassistant.task.TaskDto
import com.tomaszwnuk.dailyassistant.task.TaskRepository
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@Suppress("unused")
@RestController
@RequestMapping("/calendars")
class CalendarController(
    private val _calendarService: CalendarService,
    private val _eventRepository: EventRepository,
    private val _taskRepository: TaskRepository
) {

    @PostMapping
    fun create(@Valid @RequestBody dto: CalendarDto): ResponseEntity<CalendarDto> {
        val created: CalendarDto = _calendarService.create(dto).toDto()
        return ResponseEntity.status(201).body(created)
    }

    @GetMapping
    fun getAll(
        @PageableDefault(
            size = 10,
            sort = ["startDate"],
            direction = Sort.Direction.ASC
        ) pageable: Pageable
    ): ResponseEntity<Page<CalendarDto>> {
        val calendarsPage: Page<CalendarDto> = _calendarService.getAll(pageable).map { it.toDto() }
        return ResponseEntity.ok(calendarsPage)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<CalendarDto> {
        val calendar: CalendarDto = _calendarService.getById(id).toDto()
        return ResponseEntity.ok(calendar)
    }

    @GetMapping("/{id}/events")
    fun getEvents(
        @PathVariable id: UUID,
        @PageableDefault(size = 10, sort = ["startDate"], direction = Sort.Direction.ASC) pageable: Pageable
    ): ResponseEntity<Page<EventDto>> {
        val events: Page<EventDto> = _eventRepository.findAllByCalendarId(id, pageable).map { it.toDto() }
        return ResponseEntity.ok(events)
    }

    @GetMapping("/{id}/tasks")
    fun getTasks(
        @PathVariable id: UUID,
        @PageableDefault(size = 10, sort = ["startDate"], direction = Sort.Direction.ASC) pageable: Pageable
    ): ResponseEntity<Page<TaskDto>> {
        val tasksPage: Page<TaskDto> = _taskRepository.findAllByCalendarId(id, pageable).map { it.toDto() }
        return ResponseEntity.ok(tasksPage)
    }

    @GetMapping("/filter")
    fun filter(
        @RequestParam(name = "name", required = false) name: String?,
        @PageableDefault(size = 10, sort = ["startDate"], direction = Sort.Direction.ASC) pageable: Pageable
    ): ResponseEntity<Page<CalendarDto>> {
        val filter = CalendarFilterDto(
            name = name
        )
        val result: Page<CalendarDto> = _calendarService.filter(filter, pageable).map { it.toDto() }
        return ResponseEntity.ok(result)
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

}
