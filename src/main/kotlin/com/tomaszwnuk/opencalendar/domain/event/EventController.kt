package com.tomaszwnuk.opencalendar.domain.event

import com.tomaszwnuk.opencalendar.domain.mapper.PageMapper.toPage
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@Suppress("unused")
@RestController
@RequestMapping("/events")
class EventController(
    private val _eventService: EventService
) {

    @PostMapping
    fun create(@Valid @RequestBody(required = true) dto: EventDto): ResponseEntity<EventDto> {
        val created: EventDto = _eventService.create(dto = dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @GetMapping
    fun getAll(
        @PageableDefault(
            size = 10,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable
    ): ResponseEntity<Page<EventDto>> {
        val events: List<EventDto> = _eventService.getAll()
        return ResponseEntity.ok(events.toPage(pageable = pageable))
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable(name = "id", required = true) id: UUID): ResponseEntity<EventDto> {
        val event: EventDto = _eventService.getById(id = id)
        return ResponseEntity.ok(event)
    }

    @GetMapping("/filter")
    fun filter(
        @RequestParam(name = "name", required = false) name: String?,
        @RequestParam(name = "description", required = false) description: String?,
        @RequestParam(
            name = "dateFrom",
            required = false
        ) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) dateFrom: String?,
        @RequestParam(
            name = "dateTo",
            required = false
        ) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) dateTo: String?,
        @RequestParam(name = "recurringPattern", required = false) recurringPattern: String?,
        @RequestParam(name = "calendarId", required = false) calendarId: UUID?,
        @RequestParam(name = "categoryId", required = false) categoryId: UUID?,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<EventDto>> {
        val filter = EventFilterDto(
            name = name,
            description = description,
            dateFrom = dateFrom?.let { LocalDateTime.parse(it) },
            dateTo = dateTo?.let { LocalDateTime.parse(it) },
            recurringPattern = recurringPattern?.let { RecurringPattern.valueOf(value = it) },
            calendarId = calendarId,
            categoryId = categoryId
        )
        val events: List<EventDto> = _eventService.filter(filter = filter)
        return ResponseEntity.ok(events.toPage(pageable = pageable))
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable(name = "id", required = true) id: UUID,
        @Valid @RequestBody(required = true) dto: EventDto
    ): ResponseEntity<EventDto> {
        val updated: EventDto = _eventService.update(id = id, dto = dto)
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable(name = "id", required = true) id: UUID): ResponseEntity<Void> {
        _eventService.delete(id = id)
        return ResponseEntity.noContent().build()
    }

}
