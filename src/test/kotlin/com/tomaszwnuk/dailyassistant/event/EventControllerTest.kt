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
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
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
    fun `should create event and return 201 Created`() {
        whenever(_eventService.create(any())).thenReturn(_sampleEvent)

        val response = _eventController.create(_sampleDto)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(_sampleDto.name, response.body?.name)
    }

    @Test
    fun `should return paginated list of events`() {
        val events: List<Event> = listOf(_sampleEvent, _sampleEvent, _sampleEvent)

        whenever(_eventService.getAll(_pageable)).thenReturn(PageImpl(events))

        val response = _eventController.getAll(_pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(3, response.body?.totalElements)
    }

    @Test
    fun `should return event by id`() {
        val id = _sampleEvent.id
        whenever(_eventService.getById(id)).thenReturn(_sampleEvent)

        val response = _eventController.getById(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(id, response.body?.id)
    }

    @Test
    fun `should return filtered list of events`() {
        val filter = EventFilterDto(name = "Standup")
        val events: List<Event> = listOf(_sampleEvent, _sampleEvent, _sampleEvent)

        whenever(_eventService.filter(eq(filter), eq(_pageable))).thenReturn(PageImpl(events))

        val response = _eventController.filter("Standup", null, null, null, null, null, null, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(3, response.body?.totalElements)
    }

    @Test
    fun `should update event and return 200 OK`() {
        val updatedEvent = _sampleEvent.copy(name = "Updated Event")
        whenever(_eventService.update(_sampleEvent.id, _sampleDto)).thenReturn(updatedEvent)

        val response = _eventController.update(_sampleEvent.id, _sampleDto)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updatedEvent.name, response.body?.name)
    }

    @Test
    fun `should delete event and return 204 No Content`() {
        doNothing().whenever(_eventService).delete(_sampleEvent.id)

        val response = _eventController.delete(_sampleEvent.id)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

}
