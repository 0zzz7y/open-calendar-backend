package com.tomaszwnuk.opencalendar.calendar

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.calendar.CalendarController
import com.tomaszwnuk.opencalendar.domain.calendar.CalendarDto
import com.tomaszwnuk.opencalendar.domain.calendar.CalendarService
import com.tomaszwnuk.opencalendar.domain.event.Event
import com.tomaszwnuk.opencalendar.domain.event.EventDto
import com.tomaszwnuk.opencalendar.domain.event.EventService
import com.tomaszwnuk.opencalendar.domain.note.Note
import com.tomaszwnuk.opencalendar.domain.note.NoteDto
import com.tomaszwnuk.opencalendar.domain.note.NoteService
import com.tomaszwnuk.opencalendar.domain.other.RecurringPattern
import com.tomaszwnuk.opencalendar.domain.task.Task
import com.tomaszwnuk.opencalendar.domain.task.TaskDto
import com.tomaszwnuk.opencalendar.domain.task.TaskService
import com.tomaszwnuk.opencalendar.domain.task.TaskStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CalendarControllerTest {

    @Mock
    private lateinit var _calendarService: CalendarService

    @Mock
    private lateinit var _eventService: EventService

    @Mock
    private lateinit var _taskService: TaskService

    @Mock
    private lateinit var _noteService: NoteService

    @InjectMocks
    private lateinit var _calendarController: CalendarController

    private lateinit var _sampleCalendar: Calendar

    private lateinit var _sampleCalendarDto: CalendarDto

    private lateinit var _pageable: Pageable

    @BeforeEach
    fun setup() {
        _sampleCalendar = Calendar(
            id = UUID.randomUUID(),
            title = "Personal",
            emoji = "\\uD83C\\uDFE0"
        )
        _sampleCalendarDto = _sampleCalendar.toDto()
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created calendar with status code 201 Created`() {
        val id: UUID = UUID.randomUUID()
        val title = "Personal"
        val emoji = "\\uD83C\\uDFE0"
        val sampleCalendar = Calendar(id = id, title = title, emoji = emoji)
        val sampleCalendarDto: CalendarDto = sampleCalendar.toDto()

        whenever(_calendarService.create(sampleCalendarDto)).thenReturn(sampleCalendar)

        val response: ResponseEntity<CalendarDto> = _calendarController.create(sampleCalendarDto)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        assertEquals(sampleCalendar.id, response.body?.id)
        assertEquals(sampleCalendar.title, response.body?.title)
        assertEquals(sampleCalendar.emoji, response.body?.emoji)

        verify(_calendarService).create(sampleCalendarDto)
    }

    @Test
    fun `should return paginated list of all calendars with status code 200 OK`() {
        val calendars: List<Calendar> = listOf(_sampleCalendar, _sampleCalendar.copy(), _sampleCalendar.copy())
        whenever(_calendarService.getAll()).thenReturn(calendars)

        val response: ResponseEntity<Page<CalendarDto>> = _calendarController.getAll(_pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(calendars.size.toLong(), response.body?.totalElements)
        assertEquals(calendars.map { it.id }, response.body?.content?.map { it.id })
        assertEquals(calendars.map { it.title }, response.body?.content?.map { it.title })

        verify(_calendarService).getAll()
    }

    @Test
    fun `should return calendar by id with status code 200 OK`() {
        val id: UUID = _sampleCalendar.id
        whenever(_calendarService.getById(id)).thenReturn(_sampleCalendar)

        val response: ResponseEntity<CalendarDto> = _calendarController.getById(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(id, response.body?.id)
        assertEquals(_sampleCalendar.title, response.body?.title)
        assertEquals(_sampleCalendar.emoji, response.body?.emoji)

        verify(_calendarService).getById(id)
    }

    @Test
    fun `should return paginated list of all calendar events with status code 200 OK`() {
        val id: UUID = _sampleCalendar.id
        val now: LocalDateTime = LocalDateTime.now()
        val event = Event(
            id = UUID.randomUUID(),
            title = "Event title",
            description = "Event description",
            startDate = now,
            endDate = now.plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendar = _sampleCalendar,
            category = null
        ).toDto()
        val events: List<EventDto> = listOf(event, event, event)

        whenever(_eventService.getAllDtosByCalendarId(id)).thenReturn(events)

        val response: ResponseEntity<Page<EventDto>> = _calendarController.getEvents(id, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(events.size.toLong(), response.body?.totalElements)
        assertEquals(events.map { it.title }, response.body?.content?.map { it.title })

        verify(_eventService).getAllDtosByCalendarId(id)
    }

    @Test
    fun `should return paginated list of all calendar tasks with status code 200 OK`() {
        val id: UUID = _sampleCalendar.id
        val task = Task(
            id = UUID.randomUUID(),
            title = "Task title",
            description = "Task description",
            status = TaskStatus.TODO,
            calendar = _sampleCalendar,
            category = null
        ).toDto()
        val tasks: List<TaskDto> = listOf(task, task, task)

        whenever(_taskService.getAllByCalendarId(id)).thenReturn(tasks)

        val response: ResponseEntity<Page<TaskDto>> = _calendarController.getTasks(id, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(tasks.size.toLong(), response.body?.totalElements)
        assertEquals(tasks.map { it.title }, response.body?.content?.map { it.title })

        verify(_taskService).getAllByCalendarId(id)
    }

    @Test
    fun `should return paginated of all calendar notes with status code 200 OK`() {
        val id: UUID = _sampleCalendar.id
        val note = Note(
            id = UUID.randomUUID(),
            title = "Note title",
            description = "Note description",
            calendar = _sampleCalendar,
            category = null
        ).toDto()
        val notes: List<NoteDto> = listOf(note, note.copy())

        whenever(_noteService.getAllByCalendarId(id)).thenReturn(notes)

        val response: ResponseEntity<Page<NoteDto>> = _calendarController.getNotes(id, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(notes.size.toLong(), response.body?.totalElements)
        assertEquals(notes.map { it.title }, response.body?.content?.map { it.title })

        verify(_noteService).getAllByCalendarId(id)
    }

    @Test
    fun `should return combined list of all calendar events, tasks and notes with status code 200 OK`() {
        val id: UUID = _sampleCalendar.id
        val now: LocalDateTime = LocalDateTime.now()

        val event = Event(
            id = UUID.randomUUID(),
            title = "Sample Event",
            description = "Event description",
            startDate = now,
            endDate = now.plusHours(2),
            recurringPattern = RecurringPattern.NONE,
            calendar = _sampleCalendar,
            category = null
        ).toDto()
        val task = Task(
            id = UUID.randomUUID(),
            title = "Sample Task",
            description = "Task description",
            status = TaskStatus.TODO,
            calendar = _sampleCalendar,
            category = null
        ).toDto()
        val note = Note(
            id = UUID.randomUUID(),
            title = "Sample Note",
            description = "Note description",
            calendar = _sampleCalendar,
            category = null
        ).toDto()

        whenever(_eventService.getAllDtosByCalendarId(id)).thenReturn(listOf(event))
        whenever(_taskService.getAllByCalendarId(id)).thenReturn(listOf(task))
        whenever(_noteService.getAllByCalendarId(id)).thenReturn(listOf(note))

        val response: ResponseEntity<List<Map<String, Any>>> = _calendarController.getAllItems(id, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(3, response.body?.size)

        val types: List<String?> = response.body?.map { it["type"] as? String }.orEmpty()
        assertTrue(types.containsAll(listOf("event", "task", "note")))
    }

    @Test
    fun `should return updated calendar with status code 200 OK`() {
        val updatedCalendar = _sampleCalendar.copy(title = "Updated Title")

        whenever(_calendarService.update(_sampleCalendar.id, _sampleCalendarDto)).thenReturn(updatedCalendar)

        val response = _calendarController.update(_sampleCalendar.id, _sampleCalendarDto)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(updatedCalendar.id, response.body?.id)
        assertEquals("Updated Title", response.body?.title)

        verify(_calendarService).update(_sampleCalendar.id, _sampleCalendarDto)
    }

    @Test
    fun `should delete calendar and return status code 204 No Content`() {
        doNothing().whenever(_calendarService).delete(_sampleCalendar.id)

        val response = _calendarController.delete(_sampleCalendar.id)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

        verify(_calendarService).delete(_sampleCalendar.id)
    }

}
