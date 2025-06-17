package com.tomaszwnuk.opencalendar.calendar

import com.tomaszwnuk.opencalendar.TestConstants
import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.calendar.CalendarController
import com.tomaszwnuk.opencalendar.domain.calendar.CalendarDto
import com.tomaszwnuk.opencalendar.domain.calendar.CalendarService
import com.tomaszwnuk.opencalendar.domain.event.EventDto
import com.tomaszwnuk.opencalendar.domain.event.EventService
import com.tomaszwnuk.opencalendar.domain.event.RecurringPattern
import com.tomaszwnuk.opencalendar.domain.note.NoteDto
import com.tomaszwnuk.opencalendar.domain.note.NoteService
import com.tomaszwnuk.opencalendar.domain.task.TaskDto
import com.tomaszwnuk.opencalendar.domain.task.TaskService
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

    private lateinit var _sampleCalendar: Calendar

    private lateinit var _sampleCalendarDto: CalendarDto

    private lateinit var _pageable: Pageable

    @BeforeEach
    fun setUp() {
        _sampleCalendar = Calendar(
            id = UUID.randomUUID(),
            name = "Test",
            emoji = "🟢",
            userId = UUID.randomUUID()
        )
        _sampleCalendarDto = CalendarDto(
            id = _sampleCalendar.id,
            name = _sampleCalendar.name,
            emoji = _sampleCalendar.emoji
        )
        _pageable = PageRequest.of(
            TestConstants.PAGEABLE_PAGE_NUMBER,
            TestConstants.PAGEABLE_PAGE_SIZE
        )
    }

    @Test
    fun `should return created calendar with status code 201 Created`() {
        whenever(
            _service.create(
                eq(_sampleCalendarDto)
            )
        ).thenReturn(_sampleCalendarDto)

        val response: ResponseEntity<CalendarDto> = _controller.create(_sampleCalendarDto)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(_sampleCalendarDto, response.body)

        verify(_service).create(eq(_sampleCalendarDto))
    }

    @Test
    fun `should return paginated list of all calendars with status code 200 OK`() {
        val calendars: List<CalendarDto> =
            listOf(_sampleCalendarDto, _sampleCalendarDto.copy(), _sampleCalendarDto.copy())
        whenever(
            _service.getAll()
        ).thenReturn(calendars)

        val response: ResponseEntity<Page<CalendarDto>> = _controller.getAll(_pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(calendars.size.toLong(), response.body?.totalElements)
        assertEquals(calendars, response.body?.content)

        verify(_service).getAll()
    }

    @Test
    fun `should return calendar by id with status code 200 OK`() {
        val id: UUID = _sampleCalendar.id
        whenever(
            _service.getById(id = id)
        ).thenReturn(_sampleCalendarDto)

        val response: ResponseEntity<CalendarDto> = _controller.getById(_sampleCalendar.id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(_sampleCalendarDto, response.body)

        verify(_service).getById(id)
    }

    @Test
    fun `should return paginated list of calendar events with status code 200 OK`() {
        val id: UUID = _sampleCalendar.id
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

        whenever(
            _eventService.getAllByCalendarId(calendarId = id)
        ).thenReturn(eventsList)

        val response: ResponseEntity<Page<EventDto>> = _controller.getEvents(id = id, pageable = _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(eventsList.size.toLong(), response.body?.totalElements)
        assertEquals(eventsList, response.body?.content)

        verify(_eventService).getAllByCalendarId(calendarId = id)
    }

    @Test
    fun `should return paginated list of calendar tasks with status code 200 OK`() {
        val id: UUID = _sampleCalendar.id
        val task = TaskDto(
            id = UUID.randomUUID(),
            name = "Test",
            description = "Test",
            calendarId = id,
            categoryId = null
        )
        val tasksList: List<TaskDto> = listOf(task, task.copy(), task.copy())

        whenever(
            _taskService.getAllByCalendarId(calendarId = id)
        ).thenReturn(tasksList)

        val response: ResponseEntity<Page<TaskDto>> = _controller.getTasks(id = id, pageable = _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(tasksList.size.toLong(), response.body?.totalElements)
        assertEquals(tasksList, response.body?.content)

        verify(_taskService).getAllByCalendarId(calendarId = id)
    }

    @Test
    fun `should return paginated list of calendar notes with status code 200 OK`() {
        val id: UUID = _sampleCalendar.id
        val note = NoteDto(
            id = UUID.randomUUID(),
            name = "Test",
            description = "Test",
            calendarId = id,
            categoryId = null
        )
        val notesList: List<NoteDto> = listOf(note, note.copy(), note.copy())

        whenever(
            _noteService.getAllByCalendarId(calendarId = id)
        ).thenReturn(notesList)

        val response: ResponseEntity<Page<NoteDto>> = _controller.getNotes(id = id, pageable = _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(notesList.size.toLong(), response.body?.totalElements)
        assertEquals(notesList, response.body?.content)

        verify(_noteService).getAllByCalendarId(calendarId = id)
    }

    @Test
    fun `should return paginated list of calendar items with status code 200 OK`() {
        val id: UUID = _sampleCalendar.id
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

    // Currently stubbing, TODO: FIX
//    @Test
//    fun `should return paginated list of filtered calendars with status code 200 OK`() {
//        val name = "Test"
//        val emoji = "🟢"
//        val filter = CalendarFilterDto(
//            name = name,
//            emoji = emoji
//        )
//        val filteredCalendars: List<CalendarDto> = listOf(_sampleCalendarDto)
//
//        whenever(
//            _service.filter(filter = filter)
//        ).thenReturn(filteredCalendars)
//
//        val response: ResponseEntity<Page<CalendarDto>> = _controller.filter(
//            name = eq(filter.name),
//            emoji = eq(filter.emoji),
//            pageable = eq(_pageable)
//        )
//
//        assertEquals(HttpStatus.OK, response.statusCode)
//        assertEquals(filteredCalendars.size.toLong(), response.body?.totalElements)
//        assertEquals(filteredCalendars, response.body?.content)
//
//        verify(_service).filter(filter = filter)
//    }

    @Test
    fun `should return updated calendar with status code 200 OK`() {
        val id: UUID = _sampleCalendar.id
        val updated: CalendarDto = _sampleCalendarDto.copy(id = UUID.randomUUID())

        whenever(
            _service.update(id = id, updated)
        ).thenReturn(updated)

        val response: ResponseEntity<CalendarDto> = _controller.update(id = id, updated)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updated, response.body)

        verify(_service).update(id = id, updated)
    }

    @Test
    fun `should delete calendar with status code 204 No Content`() {
        doNothing().whenever(_service).delete(id = _sampleCalendar.id)

        val response: ResponseEntity<Void> = _controller.delete(id = _sampleCalendar.id)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

        verify(_service).delete(id = _sampleCalendar.id)
    }

}
