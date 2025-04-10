package com.tomaszwnuk.dailyassistant.calendar

import com.tomaszwnuk.dailyassistant.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.dailyassistant.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.dailyassistant.domain.RecurringPattern
import com.tomaszwnuk.dailyassistant.event.Event
import com.tomaszwnuk.dailyassistant.event.EventDto
import com.tomaszwnuk.dailyassistant.event.EventRepository
import com.tomaszwnuk.dailyassistant.task.Task
import com.tomaszwnuk.dailyassistant.task.TaskDto
import com.tomaszwnuk.dailyassistant.task.TaskRepository
import com.tomaszwnuk.dailyassistant.task.TaskStatus
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
import org.mockito.kotlin.eq
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
    private lateinit var _eventRepository: EventRepository

    @Mock
    private lateinit var _taskRepository: TaskRepository

    @InjectMocks
    private lateinit var _calendarController: CalendarController

    private lateinit var _sampleCalendar: Calendar

    private lateinit var _sampleDto: CalendarDto

    private lateinit var _pageable: Pageable

    @BeforeEach
    fun setup() {
        _sampleCalendar = Calendar(
            id = UUID.randomUUID(),
            name = "Personal"
        )
        _sampleDto = _sampleCalendar.toDto()
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created calendar with status code 201 Created`() {
        whenever(_calendarService.create(_sampleDto)).thenReturn(_sampleCalendar)
        val response: ResponseEntity<CalendarDto> = _calendarController.create(_sampleDto)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(_sampleDto.name, response.body?.name)
        verify(_calendarService).create(_sampleDto)
    }

    @Test
    fun `should return paginated list of calendars with status code 200 OK`() {
        val calendars: List<Calendar> = listOf(_sampleCalendar, _sampleCalendar, _sampleCalendar)

        whenever(_calendarService.getAll(_pageable)).thenReturn(PageImpl(calendars))
        val response: ResponseEntity<Page<CalendarDto>> = _calendarController.getAll(_pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(calendars.size, response.body?.totalElements?.toInt())
        verify(_calendarService).getAll(_pageable)
    }

    @Test
    fun `should return filtered list of calendars with status code 200 OK`() {
        val filter = CalendarFilterDto(name = "Personal")
        val calendars: List<Calendar> = listOf(_sampleCalendar, _sampleCalendar, _sampleCalendar)

        whenever(_calendarService.filter(filter, _pageable)).thenReturn(PageImpl(calendars))
        val response: ResponseEntity<Page<CalendarDto>> = _calendarController.filter(eq(filter.name), eq(_pageable))

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(calendars.size, response.body?.totalElements?.toInt())
        verify(_calendarService).filter(eq(filter), eq(_pageable))
    }

    @Test
    fun `should return calendar by id with status code 200 OK`() {
        val id: UUID = _sampleCalendar.id

        whenever(_calendarService.getById(id)).thenReturn(_sampleCalendar)
        val response: ResponseEntity<CalendarDto> = _calendarController.getById(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(id, response.body?.id)
        verify(_calendarService).getById(id)
    }

    @Test
    fun `should return events by calendar id with status code 200 OK`() {
        val id: UUID = _sampleCalendar.id
        val event = Event(
            id = UUID.randomUUID(),
            name = "Event",
            description = "Description",
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendar = _sampleCalendar,
            category = null
        )
        val events: List<Event> = listOf(event, event, event)

        whenever(_eventRepository.findAllByCalendarId(id, _pageable)).thenReturn(PageImpl(events))
        val response: ResponseEntity<Page<EventDto>> = _calendarController.getEvents(id, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(events.size, response.body?.size)
        assertEquals(events[0].name, response.body?.content?.get(0)?.name)
    }

    @Test
    fun `should return tasks by calendar id with status code 200 OK`() {
        val id: UUID = _sampleCalendar.id
        val task = Task(
            id = UUID.randomUUID(),
            name = "Task",
            description = "Description",
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            status = TaskStatus.TODO,
            calendar = _sampleCalendar,
            category = null,
        )
        val tasks: List<Task> = listOf(task, task, task)

        whenever(_taskRepository.findAllByCalendarId(id, _pageable)).thenReturn(PageImpl(tasks))
        val response: ResponseEntity<Page<TaskDto>> = _calendarController.getTasks(id, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(tasks.size, response.body?.size)
        assertEquals(tasks[0].name, response.body?.content?.get(0)?.name)
    }

    @Test
    fun `should return calendar items by calendar id with status code 200 OK`() {
        val id: UUID = _sampleCalendar.id
        val event = Event(
            id = UUID.randomUUID(),
            name = "Event",
            description = "Description",
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendar = _sampleCalendar,
            category = null
        )
        val task = Task(
            id = UUID.randomUUID(),
            name = "Task",
            description = "Description",
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            status = TaskStatus.TODO,
            calendar = _sampleCalendar,
            category = null,
        )

        whenever(_eventRepository.findAllByCalendarId(id, _pageable)).thenReturn(PageImpl(listOf(event)))
        whenever(_taskRepository.findAllByCalendarId(id, _pageable)).thenReturn(PageImpl(listOf(task)))
        val response: ResponseEntity<List<Map<String, Any>>> = _calendarController.getAllItems(id, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(2, response.body?.size)

        val types: List<Any?> = response.body?.map { it["type"] } ?: emptyList()
        assertTrue(types.containsAll(listOf("event", "task")))
    }

    @Test
    fun `should return updated calendar with status code 200 OK`() {
        val updated: Calendar = _sampleCalendar.copy(name = "Updated Calendar")

        whenever(_calendarService.update(_sampleCalendar.id, _sampleDto)).thenReturn(updated)
        val response: ResponseEntity<CalendarDto> = _calendarController.update(_sampleCalendar.id, _sampleDto)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updated.name, response.body?.name)
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
