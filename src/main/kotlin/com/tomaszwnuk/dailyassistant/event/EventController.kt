package com.tomaszwnuk.dailyassistant.event

import com.tomaszwnuk.dailyassistant.domain.RecurringPattern
import jakarta.validation.Valid
import org.springframework.cache.annotation.CacheEvict
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
    @CacheEvict
    fun create(@Valid @RequestBody dto: EventDto): ResponseEntity<EventDto> {
        val created: EventDto = _eventService.create(dto).toDto()
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
        val events: Page<EventDto> = _eventService.getAll(pageable).map { it.toDto() }
        return ResponseEntity.ok(events)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<EventDto> {
        val event: EventDto = _eventService.getById(id).toDto()
        return ResponseEntity.ok(event)
    }

    @GetMapping("/filter")
    fun filter(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) description: String?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) dateFrom: String?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) dateTo: String?,
        @RequestParam(required = false) recurringPattern: String?,
        @RequestParam(required = false) calendarId: UUID?,
        @RequestParam(required = false) categoryId: UUID?,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<EventDto>> {
        val filter = EventFilterDto(
            name = name,
            description = description,
            dateFrom = dateFrom?.let { LocalDateTime.parse(it) },
            dateTo = dateTo?.let { LocalDateTime.parse(it) },
            recurringPattern = recurringPattern?.let { RecurringPattern.valueOf(it) },
            calendarId = calendarId,
            categoryId = categoryId
        )
        val eventPages: Page<EventDto> = _eventService.filter(filter, pageable).map { it.toDto() }
        return ResponseEntity.ok(eventPages)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @Valid @RequestBody dto: EventDto): ResponseEntity<EventDto> {
        val updated: EventDto = _eventService.update(id, dto).toDto()
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        _eventService.delete(id)
        return ResponseEntity.noContent().build()
    }

}
