package com.ozzz7y.opencalendar.domain.event

import com.ozzz7y.opencalendar.domain.communication.CommunicationConstants
import com.ozzz7y.opencalendar.domain.mapper.PageMapper.toPage
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
 * The controller for managing events.
 */
@Suppress("unused")
@RestController
@RequestMapping("/${CommunicationConstants.API}/${CommunicationConstants.API_VERSION}/events")
class EventController(

    /**
     * The service for performing operations on events.
     */
    private val _eventService: EventService

) {

    /**
     * Creates a new event.
     *
     * @param dto The data transfer object containing the details of the event
     *
     * @return A response containing the created event
     */
    @PostMapping
    fun create(@Valid @RequestBody(required = true) dto: EventDto): ResponseEntity<EventDto> {
        val created: EventDto = _eventService.create(dto = dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    /**
     * Retrieves all events.
     *
     * @param pageable The pagination information
     *
     * @return A response containing a page of events
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
        return ResponseEntity.ok(events.toPage(pageable = pageable))
    }

    /**
     * Retrieves an event by its unique identifier.
     *
     * @param id The unique identifier of the event
     *
     * @return A response containing the event with the specified identifier
     */
    @GetMapping("/{id}")
    fun getById(@PathVariable(name = "id", required = true) id: UUID): ResponseEntity<EventDto> {
        val event: EventDto = _eventService.getById(id = id)
        return ResponseEntity.ok(event)
    }

    /**
     * Retrieves filtered events based on provided criteria.
     *
     * @param name The name of the event (optional)
     * @param description The description of the event (optional)
     * @param dateFrom The start date and time of the event (optional)
     * @param dateTo The end date and time of the event (optional)
     * @param recurringPattern The recurring pattern of the event (optional)
     * @param calendarId The unique identifier of the calendar (optional)
     * @param categoryId The unique identifier of the category (optional)
     * @param pageable The pagination information
     *
     * @return A response containing a page of filtered events
     */
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

    /**
     * Updates an existing event.
     *
     * @param id The unique identifier of the event to update
     * @param dto The data transfer object containing the updated details of the event
     *
     * @return A response containing the updated event
     */
    @PutMapping("/{id}")
    fun update(
        @PathVariable(name = "id", required = true) id: UUID,
        @Valid @RequestBody(required = true) dto: EventDto
    ): ResponseEntity<EventDto> {
        val updated: EventDto = _eventService.update(id = id, dto = dto)
        return ResponseEntity.ok(updated)
    }

    /**
     * Deletes an event by its unique identifier.
     *
     * @param id The unique identifier of the event to delete
     *
     * @return A response indicating the deletion status
     */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable(name = "id", required = true) id: UUID): ResponseEntity<Void> {
        _eventService.delete(id = id)
        return ResponseEntity.noContent().build()
    }

}
