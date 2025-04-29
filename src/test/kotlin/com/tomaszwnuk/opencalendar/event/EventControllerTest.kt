package com.tomaszwnuk.opencalendar.event

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.category.CategoryColorHelper
import com.tomaszwnuk.opencalendar.domain.event.*
import com.tomaszwnuk.opencalendar.domain.other.RecurringPattern
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
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
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.awt.Color
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

    private lateinit var _sampleEventDto: EventDto

    private lateinit var _pageable: Pageable

    @BeforeEach
    fun setup() {
        val sampleCalendar = Calendar(
            id = UUID.randomUUID(),
            title = "Work",
            emoji = "\uD83C\uDFE2"
        )
        val sampleCategory = Category(
            id = UUID.randomUUID(),
            title = "Meetings",
            color = CategoryColorHelper.toHex(Color.BLUE)
        )
        _sampleEvent = Event(
            id = UUID.randomUUID(),
            title = "Daily Standup",
            description = "Team sync",
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().plusHours(1),
            recurringPattern = RecurringPattern.DAILY,
            calendar = sampleCalendar,
            category = sampleCategory
        )
        _sampleEventDto = _sampleEvent.toDto()
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created event with status code 201 Created`() {
        whenever(_eventService.create(_sampleEventDto)).thenReturn(_sampleEvent)

        val response: ResponseEntity<EventDto> = _eventController.create(_sampleEventDto)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        assertEquals(_sampleEvent.id, response.body?.id)
        assertEquals(_sampleEvent.title, response.body?.title)
        assertEquals(_sampleEvent.description, response.body?.description)

        verify(_eventService).create(_sampleEventDto)
    }

    @Test
    fun `should return paginated list of all events with status code 200 OK`() {
        val events: List<Event> = listOf(_sampleEvent, _sampleEvent.copy(), _sampleEvent.copy())
        whenever(_eventService.getAll()).thenReturn(events)

        val response: ResponseEntity<Page<EventDto>> = _eventController.getAll(_pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(events.size.toLong(), response.body?.totalElements)
        assertEquals(events.map { it.id }, response.body?.content?.map { it.id })
        assertEquals(events.map { it.title }, response.body?.content?.map { it.title })

        verify(_eventService).getAll()
    }

    @Test
    fun `should return paginated list of filtered events with status code 200 OK`() {
        val filter = EventFilterDto(title = "Standup")
        val events: List<Event> = listOf(_sampleEvent, _sampleEvent.copy(), _sampleEvent.copy())
        whenever(_eventService.filter(eq(filter))).thenReturn(events)

        val response: ResponseEntity<Page<EventDto>> = _eventController.filter(
            filter.title,
            null,
            null,
            null,
            null,
            null,
            null,
            _pageable
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(events.size.toLong(), response.body?.totalElements)
        assertEquals(events.map { it.title }, response.body?.content?.map { it.title })

        verify(_eventService).filter(eq(filter))
    }

    @Test
    fun `should return event by id with status code 200 OK`() {
        val id: UUID = _sampleEvent.id
        whenever(_eventService.getById(id)).thenReturn(_sampleEvent)

        val response: ResponseEntity<EventDto> = _eventController.getById(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(_sampleEvent.id, response.body?.id)
        assertEquals(_sampleEvent.title, response.body?.title)
        assertEquals(_sampleEvent.description, response.body?.description)

        verify(_eventService).getById(id)
    }

    @Test
    fun `should return updated event with status code 200 OK`() {
        val updatedEvent: Event = _sampleEvent.copy(title = "Updated Event")
        whenever(_eventService.update(_sampleEvent.id, _sampleEventDto)).thenReturn(updatedEvent)

        val response: ResponseEntity<EventDto> = _eventController.update(_sampleEvent.id, _sampleEventDto)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(updatedEvent.id, response.body?.id)
        assertEquals("Updated Event", response.body?.title)
        assertEquals(updatedEvent.description, response.body?.description)

        verify(_eventService).update(_sampleEvent.id, _sampleEventDto)
    }

    @Test
    fun `should delete event with status code 204 No Content`() {
        doNothing().whenever(_eventService).delete(_sampleEvent.id)

        val response: ResponseEntity<Void> = _eventController.delete(_sampleEvent.id)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

        verify(_eventService).delete(_sampleEvent.id)
    }

}
