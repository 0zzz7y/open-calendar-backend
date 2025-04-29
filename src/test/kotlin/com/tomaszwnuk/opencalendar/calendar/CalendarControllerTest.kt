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
    private lateinit var calendarService: CalendarService

    @Mock
    private lateinit var eventService: EventService

    @Mock
    private lateinit var taskService: TaskService

    @Mock
    private lateinit var noteService: NoteService

    @InjectMocks
    private lateinit var calendarController: CalendarController

    private lateinit var sampleCalendar: Calendar

    private lateinit var sampleCalendarDto: CalendarDto

    private lateinit var pageable: Pageable

    @BeforeEach
    fun setup() {
        sampleCalendar = Calendar(
            id = UUID.randomUUID(),
            title = "Personal",
            emoji = "\\uD83C\\uDFE0"
        )
        sampleCalendarDto = sampleCalendar.toDto()
        pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created calendar with status code 201 Created`() {
        val id: UUID = UUID.randomUUID()
        val title = "Personal"
        val emoji = "\\uD83C\\uDFE0"
        val sampleCalendar = Calendar(id = id, title = title, emoji = emoji)
        val sampleCalendarDto: CalendarDto = sampleCalendar.toDto()

        whenever(calendarService.create(sampleCalendarDto)).thenReturn(sampleCalendar)

        val response: ResponseEntity<CalendarDto> = calendarController.create(sampleCalendarDto)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        assertEquals(sampleCalendar.id, response.body?.id)
        assertEquals(sampleCalendar.title, response.body?.title)
        assertEquals(sampleCalendar.emoji, response.body?.emoji)

        verify(calendarService).create(sampleCalendarDto)
    }

    @Test
    fun `should return paginated list of all calendars with status code 200 OK`() {
        val calendars: List<Calendar> = listOf(sampleCalendar, sampleCalendar.copy(), sampleCalendar.copy())
        whenever(calendarService.getAll(pageable)).thenReturn(PageImpl(calendars))

        val response: ResponseEntity<Page<CalendarDto>> = calendarController.getAll(pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(calendars.size.toLong(), response.body?.totalElements)
        assertEquals(calendars.map { it.id }, response.body?.content?.map { it.id })
        assertEquals(calendars.map { it.title }, response.body?.content?.map { it.title })

        verify(calendarService).getAll(pageable)
    }

    @Test
    fun `should return calendar by id with status code 200 OK`() {
        val id: UUID = sampleCalendar.id
        whenever(calendarService.getById(id)).thenReturn(sampleCalendar)

        val response: ResponseEntity<CalendarDto> = calendarController.getById(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(id, response.body?.id)
        assertEquals(sampleCalendar.title, response.body?.title)
        assertEquals(sampleCalendar.emoji, response.body?.emoji)

        verify(calendarService).getById(id)
    }

    @Test
    fun `should return paginated list of all calendar events with status code 200 OK`() {
        val id: UUID = sampleCalendar.id
        val now: LocalDateTime = LocalDateTime.now()
        val event = Event(
            id = UUID.randomUUID(),
            title = "Event title",
            description = "Event description",
            startDate = now,
            endDate = now.plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendar = sampleCalendar,
            category = null
        )
        val events: List<Event> = listOf(event, event, event)

        whenever(eventService.getAllByCalendarId(id, pageable)).thenReturn(PageImpl(events))

        val response: ResponseEntity<Page<EventDto>> = calendarController.getEvents(id, pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(events.size.toLong(), response.body?.totalElements)
        assertEquals(events.map { it.title }, response.body?.content?.map { it.title })

        verify(eventService).getAllByCalendarId(id, pageable)
    }

    @Test
    fun `should return paginated list of all calendar tasks with status code 200 OK`() {
        val id: UUID = sampleCalendar.id
        val task = Task(
            id = UUID.randomUUID(),
            title = "Task title",
            description = "Task description",
            status = TaskStatus.TODO,
            calendar = sampleCalendar,
            category = null
        )
        val tasks: List<Task> = listOf(task, task, task)

        whenever(taskService.getAllByCalendarId(id, pageable)).thenReturn(PageImpl(tasks))

        val response: ResponseEntity<Page<TaskDto>> = calendarController.getTasks(id, pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(tasks.size.toLong(), response.body?.totalElements)
        assertEquals(tasks.map { it.title }, response.body?.content?.map { it.title })

        verify(taskService).getAllByCalendarId(id, pageable)
    }

    @Test
    fun `should return paginated of all calendar notes with status code 200 OK`() {
        val id: UUID = sampleCalendar.id
        val note = Note(
            id = UUID.randomUUID(),
            title = "Note title",
            description = "Note description",
            calendar = sampleCalendar,
            category = null
        )
        val notes: List<Note> = listOf(note, note.copy())

        whenever(noteService.getAllByCalendarId(id, pageable)).thenReturn(PageImpl(notes))

        val response: ResponseEntity<Page<NoteDto>> = calendarController.getNotes(id, pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(notes.size.toLong(), response.body?.totalElements)
        assertEquals(notes.map { it.title }, response.body?.content?.map { it.title })

        verify(noteService).getAllByCalendarId(id, pageable)
    }

    @Test
    fun `should return combined list of all calendar events, tasks and notes with status code 200 OK`() {
        val id: UUID = sampleCalendar.id
        val now: LocalDateTime = LocalDateTime.now()

        val event = Event(
            id = UUID.randomUUID(),
            title = "Sample Event",
            description = "Event description",
            startDate = now,
            endDate = now.plusHours(2),
            recurringPattern = RecurringPattern.NONE,
            calendar = sampleCalendar,
            category = null
        )
        val task = Task(
            id = UUID.randomUUID(),
            title = "Sample Task",
            description = "Task description",
            status = TaskStatus.TODO,
            calendar = sampleCalendar,
            category = null
        )
        val note = Note(
            id = UUID.randomUUID(),
            title = "Sample Note",
            description = "Note description",
            calendar = sampleCalendar,
            category = null
        )

        whenever(eventService.getAllByCalendarId(id, pageable)).thenReturn(PageImpl(listOf(event)))
        whenever(taskService.getAllByCalendarId(id, pageable)).thenReturn(PageImpl(listOf(task)))
        whenever(noteService.getAllByCalendarId(id, pageable)).thenReturn(PageImpl(listOf(note)))

        val response: ResponseEntity<List<Map<String, Any>>> = calendarController.getAllItems(id, pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(3, response.body?.size)

        val types: List<String?> = response.body?.map { it["type"] as? String }.orEmpty()
        assertTrue(types.containsAll(listOf("event", "task", "note")))
    }

    @Test
    fun `should return updated calendar with status code 200 OK`() {
        val updatedCalendar = sampleCalendar.copy(title = "Updated Title")

        whenever(calendarService.update(sampleCalendar.id, sampleCalendarDto)).thenReturn(updatedCalendar)

        val response = calendarController.update(sampleCalendar.id, sampleCalendarDto)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(updatedCalendar.id, response.body?.id)
        assertEquals("Updated Title", response.body?.title)

        verify(calendarService).update(sampleCalendar.id, sampleCalendarDto)
    }

    @Test
    fun `should delete calendar and return status code 204 No Content`() {
        doNothing().whenever(calendarService).delete(sampleCalendar.id)

        val response = calendarController.delete(sampleCalendar.id)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

        verify(calendarService).delete(sampleCalendar.id)
    }

}
