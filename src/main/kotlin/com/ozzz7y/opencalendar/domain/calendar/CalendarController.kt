package com.ozzz7y.opencalendar.domain.calendar

import com.ozzz7y.opencalendar.domain.communication.CommunicationConstants
import com.ozzz7y.opencalendar.domain.event.EventDto
import com.ozzz7y.opencalendar.domain.event.EventService
import com.ozzz7y.opencalendar.domain.mapper.ItemTypeMapper.toMapWithType
import com.ozzz7y.opencalendar.domain.mapper.PageMapper.toPage
import com.ozzz7y.opencalendar.domain.note.NoteDto
import com.ozzz7y.opencalendar.domain.note.NoteService
import com.ozzz7y.opencalendar.domain.task.TaskDto
import com.ozzz7y.opencalendar.domain.task.TaskService
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
 * The controller for managing calendars.
 */
@Suppress("unused")
@RestController
@RequestMapping("/${CommunicationConstants.API}/${CommunicationConstants.API_VERSION}/calendars")
class CalendarController(

    /**
     * The service for performing operations on calendars.
     */
    private val _calendarService: CalendarService,

    /**
     * The service for performing operations on events.
     */
    private val _eventService: EventService,

    /**
     * The service for performing operations on tasks.
     */
    private val _taskService: TaskService,

    /**
     * The service for performing operations on notes.
     */
    private val _noteService: NoteService

) {

    /**
     * Creates a new calendar.
     *
     * @param dto The data transfer object containing the details of the calendar
     *
     * @return A response containing the created calendar
     */
    @PostMapping
    fun create(@Valid @RequestBody(required = true) dto: CalendarDto): ResponseEntity<CalendarDto> {
        val created: CalendarDto = _calendarService.create(dto = dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    /**
     * Retrieves all calendars.
     *
     * @param pageable The pagination information
     *
     * @return A response containing a page of calendars
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
        return ResponseEntity.ok(calendars.toPage(pageable = pageable))
    }

    /**
     * Retrieves a calendar by its unique identifier.
     *
     * @param id The unique identifier of the calendar
     *
     * @return A response containing the calendar with the specified identifier
     */
    @GetMapping("/{id}")
    fun getById(@PathVariable(name = "id", required = true) id: UUID): ResponseEntity<CalendarDto> {
        val calendar: CalendarDto = _calendarService.getById(id = id)
        return ResponseEntity.ok(calendar)
    }

    /**
     * Retrieves all events associated with a calendar.
     *
     * @param id The unique identifier of the calendar
     * @param pageable The pagination information
     *
     * @return A response containing a page of events associated with the calendar
     */
    @GetMapping("/{id}/events")
    fun getEvents(
        @PathVariable(name = "id", required = true) id: UUID,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<EventDto>> {
        val events: List<EventDto> = _eventService.getAllByCalendarId(calendarId = id)
        return ResponseEntity.ok(events.toPage(pageable = pageable))
    }

    /**
     * Retrieves all tasks associated with a calendar.
     *
     * @param id The unique identifier of the calendar
     * @param pageable The pagination information
     *
     * @return A response containing a page of tasks associated with the calendar
     */
    @GetMapping("/{id}/tasks")
    fun getTasks(
        @PathVariable(name = "id", required = true) id: UUID,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<TaskDto>> {
        val tasks: List<TaskDto> = _taskService.getAllByCalendarId(calendarId = id)
        return ResponseEntity.ok(tasks.toPage(pageable = pageable))
    }

    /**
     * Retrieves all notes associated with a calendar.
     *
     * @param id The unique identifier of the calendar
     * @param pageable The pagination information
     *
     * @return A response containing a page of notes associated with the calendar
     */
    @GetMapping("/{id}/notes")
    fun getNotes(
        @PathVariable(name = "id", required = true) id: UUID,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<NoteDto>> {
        val notes: List<NoteDto> = _noteService.getAllByCalendarId(calendarId = id)
        return ResponseEntity.ok(notes.toPage(pageable = pageable))
    }

    /**
     * Retrieves all items associated with a calendar.
     *
     * @param id The unique identifier of the calendar
     * @param pageable The pagination information
     *
     * @return A response containing a list of items associated with the calendar
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
        val events: Page<Map<String, Any>> = _eventService.getAllByCalendarId(calendarId = id).map {
            it.toMapWithType(type = "event")
        }.toPage(pageable)
        val tasks: Page<Map<String, Any>> = _taskService.getAllByCalendarId(calendarId = id).map {
            it.toMapWithType(type = "task")
        }.toPage(pageable)
        val notes: Page<Map<String, Any>> = _noteService.getAllByCalendarId(calendarId = id).map {
            it.toMapWithType(type = "note")
        }.toPage(pageable)

        val items: List<Map<String, Any>> = events + tasks + notes
        return ResponseEntity.ok(items)
    }

    /**
     * Filters calendars based on the provided criteria.
     *
     * @param name The name of the calendar to filter by (optional)
     * @param emoji The emoji of the calendar to filter by (optional)
     * @param pageable The pagination information
     *
     * @return A response containing a page of calendars that match the filter criteria
     */
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

    /**
     * Updates an existing calendar.
     *
     * @param id The unique identifier of the calendar to update
     * @param dto The data transfer object containing the updated details of the calendar
     *
     * @return A response containing the updated calendar
     */
    @PutMapping("/{id}")
    fun update(
        @PathVariable(name = "id", required = true) id: UUID,
        @Valid @RequestBody dto: CalendarDto
    ): ResponseEntity<CalendarDto> {
        val updated: CalendarDto = _calendarService.update(id = id, dto = dto)
        return ResponseEntity.ok(updated)
    }

    /**
     * Deletes a calendar by its unique identifier.
     *
     * @param id The unique identifier of the calendar to delete
     *
     * @return A response indicating the deletion was successful
     */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable(name = "id", required = true) id: UUID): ResponseEntity<Void> {
        _eventService.deleteAllByCalendarId(calendarId = id)
        _taskService.deleteAllByCalendarId(calendarId = id)
        _noteService.deleteByCalendarId(calendarId = id)
        _calendarService.delete(id)
        return ResponseEntity.noContent().build()
    }

}
