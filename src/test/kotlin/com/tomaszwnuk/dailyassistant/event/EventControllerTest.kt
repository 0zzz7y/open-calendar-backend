package com.tomaszwnuk.dailyassistant.event

import com.tomaszwnuk.dailyassistant.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.dailyassistant.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.dailyassistant.calendar.Calendar
import com.tomaszwnuk.dailyassistant.category.Category
import com.tomaszwnuk.dailyassistant.domain.RecurringPattern
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.*
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
class EventControllerTest {

    @Mock
    private lateinit var _eventService: EventService

    @InjectMocks
    private lateinit var _eventController: EventController

    private lateinit var _sampleEvent: Event

    private lateinit var _sampleDto: EventDto

    private lateinit var _pageable: Pageable

    @BeforeEach
    fun setup() {
        val sampleCalendar = Calendar(name = "Work")
        val sampleCategory = Category(name = "Meetings")
        _sampleEvent = Event(
            id = UUID.randomUUID(),
            name = "Daily Standup",
            description = "Team sync",
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().plusHours(1),
            recurringPattern = RecurringPattern.DAILY,
            calendar = sampleCalendar,
            category = sampleCategory
        )
        _sampleDto = _sampleEvent.toDto()
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created event with status code 201 Created`() {
        whenever(_eventService.create(any())).thenReturn(_sampleEvent)
        val response: ResponseEntity<EventDto> = _eventController.create(_sampleDto)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(_sampleDto, response.body)
        verify(_eventService).create(_sampleDto)
    }

    @Test
    fun `should return paginated list of events with status code 200 OK`() {
        val events: List<Event> = listOf(_sampleEvent, _sampleEvent, _sampleEvent)

        whenever(_eventService.getAll(_pageable)).thenReturn(PageImpl(events))
        val response: ResponseEntity<Page<EventDto>> = _eventController.getAll(_pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(events.size, response.body?.totalElements?.toInt())
        verify(_eventService).getAll(_pageable)
    }

    @Test
    fun `should return event by id with status code 200 OK`() {
        val id: UUID = _sampleEvent.id

        whenever(_eventService.getById(id)).thenReturn(_sampleEvent)
        val response: ResponseEntity<EventDto> = _eventController.getById(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(id, response.body?.id)
        verify(_eventService).getById(id)
    }

    @Test
    fun `should return filtered list of events with status code 200 OK`() {
        val filter = EventFilterDto(name = "Standup")
        val events: List<Event> = listOf(_sampleEvent, _sampleEvent, _sampleEvent)

        whenever(_eventService.filter(eq(filter), eq(_pageable))).thenReturn(PageImpl(events))
        val response: ResponseEntity<Page<EventDto>> = _eventController.filter(
            eq(filter.name),
            null,
            null,
            null,
            null,
            null,
            null,
            eq(_pageable)
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(events.size, response.body?.totalElements?.toInt())
        verify(_eventService).filter(eq(filter), eq(_pageable))
    }

    @Test
    fun `should return updated event with status code 200 OK`() {
        val updated: Event = _sampleEvent.copy(name = "Updated Event")

        whenever(_eventService.update(_sampleEvent.id, _sampleDto)).thenReturn(updated)
        val response: ResponseEntity<EventDto> = _eventController.update(_sampleEvent.id, _sampleDto)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updated.toDto(), response.body)
        verify(_eventService).update(_sampleEvent.id, _sampleDto)
    }

    @Test
    fun `should delete event with status code 204 No Content`() {
        doNothing().whenever(_eventService).delete(_sampleEvent.id)
        val response: ResponseEntity<Void> = _eventController.delete(_sampleEvent.id)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        verify(_eventService).delete(_sampleEvent.id)
    }

}
