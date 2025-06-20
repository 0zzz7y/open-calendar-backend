package com.ozzz7y.opencalendar.calendar

import com.ozzz7y.opencalendar.TestConstants
import com.ozzz7y.opencalendar.domain.calendar.CalendarController
import com.ozzz7y.opencalendar.domain.calendar.CalendarDto
import com.ozzz7y.opencalendar.domain.calendar.CalendarService
import com.ozzz7y.opencalendar.domain.event.EventDto
import com.ozzz7y.opencalendar.domain.event.EventService
import com.ozzz7y.opencalendar.domain.event.RecurringPattern
import com.ozzz7y.opencalendar.domain.note.NoteDto
import com.ozzz7y.opencalendar.domain.note.NoteService
import com.ozzz7y.opencalendar.domain.task.TaskDto
import com.ozzz7y.opencalendar.domain.task.TaskService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
internal class CalendarControllerTest {

    @Mock
    private lateinit var _service: CalendarService

    @Mock
    private lateinit var _eventService: EventService

    @Mock
    private lateinit var _taskService: TaskService

    @Mock
    private lateinit var _noteService: NoteService

    @InjectMocks
    private lateinit var _controller: CalendarController

    private lateinit var _dto: CalendarDto

    private lateinit var _pageable: Pageable

    @BeforeEach
    fun setUp() {
        _dto = CalendarDto(
            id = UUID.randomUUID(),
            name = "Test",
            emoji = "🟢"
        )
        _pageable = PageRequest.of(
            TestConstants.PAGEABLE_PAGE_NUMBER,
            TestConstants.PAGEABLE_PAGE_SIZE
        )
    }

    @Test
    fun `should return created calendar with status code 201 Created`() {
        whenever(_service.create(_dto)).thenReturn(_dto)

        val response: ResponseEntity<CalendarDto> = _controller.create(dto = _dto)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(_dto, response.body)

        verify(_service).create(eq(_dto))
    }

    @Test
    fun `should return paginated list of all calendars with status code 200 OK`() {
        val dtos: List<CalendarDto> = listOf(_dto, _dto.copy(), _dto.copy())

        whenever(_service.getAll()).thenReturn(dtos)

        val response: ResponseEntity<Page<CalendarDto>> = _controller.getAll(pageable = _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(dtos.size.toLong(), response.body?.totalElements)
        assertEquals(dtos, response.body?.content)

        verify(_service).getAll()
    }

    @Test
    fun `should return calendar by id with status code 200 OK`() {
        val id: UUID = _dto.id!!

        whenever(_service.getById(id = id)).thenReturn(_dto)

        val response: ResponseEntity<CalendarDto> = _controller.getById(id = id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(_dto, response.body)

        verify(_service).getById(id = id)
    }

    @Test
    fun `should return paginated list of calendar events with status code 200 OK`() {
        val id: UUID = _dto.id!!
        val now: LocalDateTime = LocalDateTime.now()
        val event = EventDto(
            id = UUID.randomUUID(),
            name = "Test",
            description = "Test",
            startDate = now,
            endDate = now.plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendarId = id,
            categoryId = null
        )
        val eventsList: List<EventDto> = listOf(event, event.copy(), event.copy())

        whenever(_eventService.getAllByCalendarId(calendarId = id)).thenReturn(eventsList)

        val response: ResponseEntity<Page<EventDto>> = _controller.getEvents(id = id, pageable = _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(eventsList.size.toLong(), response.body?.totalElements)
        assertEquals(eventsList, response.body?.content)

        verify(_eventService).getAllByCalendarId(calendarId = id)
    }

    @Test
    fun `should return paginated list of calendar tasks with status code 200 OK`() {
        val id: UUID = _dto.id!!
        val task = TaskDto(
            id = UUID.randomUUID(),
            name = "Test",
            description = "Test",
            calendarId = id,
            categoryId = null
        )
        val tasksList: List<TaskDto> = listOf(task, task.copy(), task.copy())

        whenever(_taskService.getAllByCalendarId(calendarId = id)).thenReturn(tasksList)

        val response: ResponseEntity<Page<TaskDto>> = _controller.getTasks(id = id, pageable = _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(tasksList.size.toLong(), response.body?.totalElements)
        assertEquals(tasksList, response.body?.content)

        verify(_taskService).getAllByCalendarId(calendarId = id)
    }

    @Test
    fun `should return paginated list of calendar notes with status code 200 OK`() {
        val id: UUID = _dto.id!!
        val note = NoteDto(
            id = UUID.randomUUID(),
            name = "Test",
            description = "Test",
            calendarId = id,
            categoryId = null
        )
        val notesList: List<NoteDto> = listOf(note, note.copy(), note.copy())

        whenever(_noteService.getAllByCalendarId(calendarId = id)).thenReturn(notesList)

        val response: ResponseEntity<Page<NoteDto>> = _controller.getNotes(id = id, pageable = _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(notesList.size.toLong(), response.body?.totalElements)
        assertEquals(notesList, response.body?.content)

        verify(_noteService).getAllByCalendarId(calendarId = id)
    }

    @Test
    fun `should return paginated list of calendar items with status code 200 OK`() {
        val id: UUID = _dto.id!!
        val now: LocalDateTime = LocalDateTime.now()
        val event = EventDto(
            id = UUID.randomUUID(),
            name = "Test",
            description = "Test",
            startDate = now,
            endDate = now.plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendarId = id,
            categoryId = null
        )
        val task = TaskDto(
            id = UUID.randomUUID(),
            name = "Test",
            description = "Test",
            calendarId = id,
            categoryId = null
        )
        val note = NoteDto(
            id = UUID.randomUUID(),
            name = "Test",
            description = "Test",
            calendarId = id,
            categoryId = null
        )

        whenever(_eventService.getAllByCalendarId(calendarId = id)).thenReturn(listOf(event))
        whenever(_taskService.getAllByCalendarId(calendarId = id)).thenReturn(listOf(task))
        whenever(_noteService.getAllByCalendarId(calendarId = id)).thenReturn(listOf(note))

        val response: ResponseEntity<List<Map<String, Any>>> = _controller.getAllItems(id = id, pageable = _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(3, response.body?.size)

        val types = response.body?.mapNotNull { it["type"] as? String } ?: emptyList()
        assertTrue(types.containsAll(listOf("event", "task", "note")))

        verify(_eventService).getAllByCalendarId(calendarId = id)
        verify(_taskService).getAllByCalendarId(calendarId = id)
        verify(_noteService).getAllByCalendarId(calendarId = id)
    }

    // Currently stubbing, TODO: Is this even fixable?
//    @Test
//    fun `should return paginated list of filtered calendars with status code 200 OK`() {
//        val name = "Test"
//        val emoji = "🟢"
//        val filter = CalendarFilterDto(
//            name = name,
//            emoji = emoji
//        )
//        val filteredDtos: List<CalendarDto> = listOf(_sampleDto)
//
//        whenever(_service.filter(filter = filter)).thenReturn(filteredDtos)
//
//        val response: ResponseEntity<Page<CalendarDto>> = _controller.filter(
//            name = filter.name,
//            emoji = filter.emoji,
//            pageable = _pageable
//        )
//
//        assertEquals(HttpStatus.OK, response.statusCode)
//        assertEquals(filteredDtos.size.toLong(), response.body?.totalElements)
//        assertEquals(filteredDtos, response.body?.content)
//
//        verify(_service).filter(filter = filter)
//    }

    @Test
    fun `should return updated calendar with status code 200 OK`() {
        val id: UUID = _dto.id!!
        val updated: CalendarDto = _dto.copy(id = UUID.randomUUID())

        whenever(_service.update(id = id, dto = updated)).thenReturn(updated)

        val response: ResponseEntity<CalendarDto> = _controller.update(id = id, dto = updated)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updated, response.body)

        verify(_service).update(id = id, dto = updated)
    }

    @Test
    fun `should delete calendar with status code 204 No Content`() {
        val id: UUID = _dto.id!!

        doNothing().whenever(_service).delete(id = id)

        val response: ResponseEntity<Void> = _controller.delete(id = id)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

        verify(_service).delete(id = id)
    }

}
