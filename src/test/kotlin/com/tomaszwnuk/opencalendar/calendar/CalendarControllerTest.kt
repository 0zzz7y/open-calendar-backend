package com.tomaszwnuk.opencalendar.calendar

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.domain.RecurringPattern
import com.tomaszwnuk.opencalendar.event.Event
import com.tomaszwnuk.opencalendar.event.EventDto
import com.tomaszwnuk.opencalendar.event.EventService
import com.tomaszwnuk.opencalendar.note.Note
import com.tomaszwnuk.opencalendar.note.NoteDto
import com.tomaszwnuk.opencalendar.note.NoteService
import com.tomaszwnuk.opencalendar.task.Task
import com.tomaszwnuk.opencalendar.task.TaskDto
import com.tomaszwnuk.opencalendar.task.TaskService
import com.tomaszwnuk.opencalendar.task.TaskStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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
import org.springframework.data.domain.PageImpl
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

    private lateinit var _sampleDto: CalendarDto

    private lateinit var _pageable: Pageable

    @BeforeEach
    fun setup() {
        _sampleCalendar = Calendar(
            id = UUID.randomUUID(),
            title = "Personal",
            emoji = "\\uD83C\\uDFE0"
        )
        _sampleDto = _sampleCalendar.toDto()
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created calendar with status code 201 Created`() {
        whenever(_calendarService.create(_sampleDto)).thenReturn(_sampleCalendar)
        val response: ResponseEntity<CalendarDto> = _calendarController.create(_sampleDto)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(_sampleCalendar.title, response.body?.title)
        assertEquals(_sampleCalendar.emoji, response.body?.emoji)
        verify(_calendarService).create(_sampleDto)
    }

    @Test
    fun `should return paginated list of calendars with status code 200 OK`() {
        val calendars: List<Calendar> = listOf(_sampleCalendar, _sampleCalendar, _sampleCalendar)

        whenever(_calendarService.getAll(_pageable)).thenReturn(PageImpl(calendars))
        val response: ResponseEntity<Page<CalendarDto>> = _calendarController.getAll(_pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(calendars.size, response.body?.totalElements?.toInt())
        assertEquals(calendars.map { it.id }, response.body?.content?.map { it.id })
        verify(_calendarService).getAll(_pageable)
    }

//    @Test
//    fun `should return paginated list of filtered calendars with status code 200 OK`() {
//        val filter = CalendarFilterDto(name = "Personal")
//        val calendars: List<Calendar> = listOf(_sampleCalendar, _sampleCalendar, _sampleCalendar)
//
//        whenever(_calendarService.filter(filter, _pageable)).thenReturn(PageImpl(calendars))
//        val response: ResponseEntity<Page<CalendarDto>> = _calendarController.filter(
//            eq(filter.name),
//            null,
//            eq(_pageable)
//        )
//
//        assertEquals(HttpStatus.OK, response.statusCode)
//        assertEquals(calendars.size, response.body?.totalElements?.toInt())
//        assertEquals(filter.name, response.body?.content?.firstOrNull()?.name)
//        verify(_calendarService).filter(filter, _pageable)
//    }

    @Test
    fun `should return calendar by id with status code 200 OK`() {
        val id: UUID = _sampleCalendar.id

        whenever(_calendarService.getById(id)).thenReturn(_sampleCalendar)
        val response: ResponseEntity<CalendarDto> = _calendarController.getById(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(id, response.body?.id)
        assertEquals(_sampleCalendar.title, response.body?.title)
        verify(_calendarService).getById(id)
    }

    @Test
    fun `should return paginated list of events by calendar id with status code 200 OK`() {
        val id: UUID = _sampleCalendar.id
        val now = LocalDateTime.now()
        val event = Event(
            id = UUID.randomUUID(),
            title = "Event",
            description = "Description",
            startDate = now,
            endDate = now.plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendar = _sampleCalendar,
            category = null
        )
        val events: List<Event> = listOf(event, event, event)

        whenever(_eventService.getAllByCalendarId(id, _pageable)).thenReturn(PageImpl(events))
        val response: ResponseEntity<Page<EventDto>> = _calendarController.getEvents(id, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(events.size, response.body?.totalElements?.toInt())
        assertEquals(events[0].title, response.body?.content?.get(0)?.title)
        verify(_eventService).getAllByCalendarId(id, _pageable)
    }

    @Test
    fun `should return paginated list of tasks by calendar id with status code 200 OK`() {
        val id: UUID = _sampleCalendar.id
        val task = Task(
            id = UUID.randomUUID(),
            title = "Task",
            description = "Description",
            status = TaskStatus.TODO,
            calendar = _sampleCalendar,
            category = null,
        )
        val tasks: List<Task> = listOf(task, task, task)

        whenever(_taskService.getAllByCalendarId(id, _pageable)).thenReturn(PageImpl(tasks))
        val response: ResponseEntity<Page<TaskDto>> = _calendarController.getTasks(id, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(tasks.size, response.body?.totalElements?.toInt())
        assertEquals(tasks[0].title, response.body?.content?.get(0)?.title)
        verify(_taskService).getAllByCalendarId(id, _pageable)
    }

    @Test
    fun `should return paginated list of notes by calendar id with status code 200 OK`() {
        val id: UUID = _sampleCalendar.id
        val note = Note(
            id = UUID.randomUUID(),
            title = "Note",
            description = "Description",
            calendar = _sampleCalendar,
            category = null
        )
        val notes: List<Note> = listOf(note)

        whenever(_noteService.getAllByCalendarId(id, _pageable)).thenReturn(PageImpl(notes))
        val response: ResponseEntity<Page<NoteDto>> = _calendarController.getNotes(id, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(notes.size, response.body?.totalElements?.toInt())
        assertEquals(notes[0].title, response.body?.content?.get(0)?.title)
        verify(_noteService).getAllByCalendarId(id, _pageable)
    }

    @Test
    fun `should return calendar items by calendar id with status code 200 OK`() {
        val id = _sampleCalendar.id
        val now = LocalDateTime.now()
        val event = Event(
            id = UUID.randomUUID(),
            title = "Event",
            description = "Description",
            startDate = now,
            endDate = now.plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendar = _sampleCalendar,
            category = null
        )
        val task = Task(
            id = UUID.randomUUID(),
            title = "Task",
            description = "Description",
            status = TaskStatus.TODO,
            calendar = _sampleCalendar,
            category = null,
        )
        val note = Note(
            id = UUID.randomUUID(),
            title = "Note",
            description = "Description",
            calendar = _sampleCalendar,
            category = null
        )

        whenever(_eventService.getAllByCalendarId(id, _pageable)).thenReturn(PageImpl(listOf(event)))
        whenever(_taskService.getAllByCalendarId(id, _pageable)).thenReturn(PageImpl(listOf(task)))
        whenever(_noteService.getAllByCalendarId(id, _pageable)).thenReturn(PageImpl(listOf(note)))

        val response: ResponseEntity<List<Map<String, Any>>> = _calendarController.getAllItems(id, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(3, response.body?.size)

        val types: List<String> = response.body?.mapNotNull { it["type"] as? String } ?: emptyList()
        assertTrue(types.containsAll(listOf("event", "task", "note")))
    }

    @Test
    fun `should return updated calendar with status code 200 OK`() {
        val updated: Calendar = _sampleCalendar.copy(title = "Updated")
        whenever(_calendarService.update(_sampleCalendar.id, _sampleDto)).thenReturn(updated)

        val response: ResponseEntity<CalendarDto> = _calendarController.update(_sampleCalendar.id, _sampleDto)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updated.title, response.body?.title)
        verify(_calendarService).update(_sampleCalendar.id, _sampleDto)
    }

    @Test
    fun `should delete calendar with status code 204 No Content`() {
        doNothing().whenever(_calendarService).delete(_sampleCalendar.id)

        val response: ResponseEntity<Void> = _calendarController.delete(_sampleCalendar.id)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        verify(_calendarService).delete(_sampleCalendar.id)
    }
}
