/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.event

import com.tomaszwnuk.opencalendar.domain.mapper.PageMapper.toPage
import com.tomaszwnuk.opencalendar.domain.other.RecurringPattern
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

/**
 * REST controller for managing events in the application.
 * Provides endpoints for creating, retrieving, filtering, updating, and deleting events.
 *
 * @property _eventService The service responsible for handling event-related operations.
 */
@Suppress("unused")
@RestController
@RequestMapping("/events")
class EventController(
    private val _eventService: EventService
) {

    /**
     * Creates a new event.
     *
     * @param dto The data transfer object containing event details.
     *
     * @return A ResponseEntity containing the created event DTO and HTTP status 201 (Created).
     */
    @PostMapping
    fun create(@Valid @RequestBody(required = true) dto: EventDto): ResponseEntity<EventDto> {
        val created: EventDto = _eventService.create(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    /**
     * Retrieves a paginated list of all events.
     *
     * @param pageable The pagination and sorting information.
     *
     * @return A ResponseEntity containing a page of event DTOs.
     */
    @GetMapping
    fun getAll(
        @PageableDefault(
            size = 10,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable
    ): ResponseEntity<Page<EventDto>> {
        val events: List<EventDto> = _eventService.getAll()
        return ResponseEntity.ok(events.toPage(pageable))
    }

    /**
     * Retrieves an event by its unique identifier.
     *
     * @param id The UUID of the event to retrieve.
     *
     * @return A ResponseEntity containing the event DTO.
     */
    @GetMapping("/{id}")
    fun getById(@PathVariable(name = "id", required = true) id: UUID): ResponseEntity<EventDto> {
        val event: EventDto = _eventService.getById(id)
        return ResponseEntity.ok(event)
    }

    /**
     * Filters events based on various criteria.
     *
     * @param title The title of the event (optional).
     * @param description The description of the event (optional).
     * @param dateFrom The start date for filtering (optional).
     * @param dateTo The end date for filtering (optional).
     * @param recurringPattern The recurring pattern of the event (optional).
     * @param calendarId The ID of the calendar to which the event belongs (optional).
     * @param categoryId The ID of the category associated with the event (optional).
     * @param pageable The pagination and sorting information.
     *
     * @return A ResponseEntity containing a page of filtered event DTOs.
     */
    @GetMapping("/filter")
    fun filter(
        @RequestParam(name = "title", required = false) title: String?,
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
            title = title,
            description = description,
            dateFrom = dateFrom?.let { LocalDateTime.parse(it) },
            dateTo = dateTo?.let { LocalDateTime.parse(it) },
            recurringPattern = recurringPattern?.let { RecurringPattern.valueOf(it) },
            calendarId = calendarId,
            categoryId = categoryId
        )
        val events: List<EventDto> = _eventService.filter(filter)
        return ResponseEntity.ok(events.toPage(pageable))
    }

    /**
     * Updates an existing event.
     *
     * @param id The UUID of the event to update.
     * @param dto The data transfer object containing updated event details.
     *
     * @return A ResponseEntity containing the updated event DTO.
     */
    @PutMapping("/{id}")
    fun update(
        @PathVariable(name = "id", required = true) id: UUID,
        @Valid @RequestBody(required = true) dto: EventDto
    ): ResponseEntity<EventDto> {
        val updated: EventDto = _eventService.update(id, dto)
        return ResponseEntity.ok(updated)
    }

    /**
     * Deletes an event by its unique identifier.
     *
     * @param id The UUID of the event to delete.
     *
     * @return A ResponseEntity with HTTP status 204 (No Content).
     */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable(name = "id", required = true) id: UUID): ResponseEntity<Void> {
        _eventService.delete(id)
        return ResponseEntity.noContent().build()
    }

}
