package com.tomaszwnuk.opencalendar.event

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.calendar.Calendar
import com.tomaszwnuk.opencalendar.category.Category
import com.tomaszwnuk.opencalendar.category.CategoryColorHelper
import com.tomaszwnuk.opencalendar.domain.RecurringPattern
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
import org.springframework.data.domain.PageImpl
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
    private lateinit var eventService: EventService

    @InjectMocks
    private lateinit var eventController: EventController

    private lateinit var sampleEvent: Event

    private lateinit var sampleEventDto: EventDto

    private lateinit var pageable: Pageable

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
        sampleEvent = Event(
            id = UUID.randomUUID(),
            title = "Daily Standup",
            description = "Team sync",
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().plusHours(1),
            recurringPattern = RecurringPattern.DAILY,
            calendar = sampleCalendar,
            category = sampleCategory
        )
        sampleEventDto = sampleEvent.toDto()
        pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created event with status code 201 Created`() {
        whenever(eventService.create(sampleEventDto)).thenReturn(sampleEvent)

        val response: ResponseEntity<EventDto> = eventController.create(sampleEventDto)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        assertEquals(sampleEvent.id, response.body?.id)
        assertEquals(sampleEvent.title, response.body?.title)
        assertEquals(sampleEvent.description, response.body?.description)

        verify(eventService).create(sampleEventDto)
    }

    @Test
    fun `should return paginated list of all events with status code 200 OK`() {
        val events: List<Event> = listOf(sampleEvent, sampleEvent.copy(), sampleEvent.copy())
        whenever(eventService.getAll(pageable)).thenReturn(PageImpl(events))

        val response: ResponseEntity<Page<EventDto>> = eventController.getAll(pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(events.size.toLong(), response.body?.totalElements)
        assertEquals(events.map { it.id }, response.body?.content?.map { it.id })
        assertEquals(events.map { it.title }, response.body?.content?.map { it.title })

        verify(eventService).getAll(pageable)
    }

    @Test
    fun `should return paginated list of filtered events with status code 200 OK`() {
        val filter = EventFilterDto(title = "Standup")
        val events: List<Event> = listOf(sampleEvent, sampleEvent.copy(), sampleEvent.copy())
        whenever(eventService.filter(eq(filter), eq(pageable))).thenReturn(PageImpl(events))

        val response: ResponseEntity<Page<EventDto>> = eventController.filter(
            filter.title,
            null,
            null,
            null,
            null,
            null,
            null,
            pageable
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(events.size.toLong(), response.body?.totalElements)
        assertEquals(events.map { it.title }, response.body?.content?.map { it.title })

        verify(eventService).filter(eq(filter), eq(pageable))
    }

    @Test
    fun `should return event by id with status code 200 OK`() {
        val id: UUID = sampleEvent.id
        whenever(eventService.getById(id)).thenReturn(sampleEvent)

        val response: ResponseEntity<EventDto> = eventController.getById(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(sampleEvent.id, response.body?.id)
        assertEquals(sampleEvent.title, response.body?.title)
        assertEquals(sampleEvent.description, response.body?.description)

        verify(eventService).getById(id)
    }

    @Test
    fun `should return updated event with status code 200 OK`() {
        val updatedEvent: Event = sampleEvent.copy(title = "Updated Event")
        whenever(eventService.update(sampleEvent.id, sampleEventDto)).thenReturn(updatedEvent)

        val response: ResponseEntity<EventDto> = eventController.update(sampleEvent.id, sampleEventDto)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(updatedEvent.id, response.body?.id)
        assertEquals("Updated Event", response.body?.title)
        assertEquals(updatedEvent.description, response.body?.description)

        verify(eventService).update(sampleEvent.id, sampleEventDto)
    }

    @Test
    fun `should delete event with status code 204 No Content`() {
        doNothing().whenever(eventService).delete(sampleEvent.id)

        val response: ResponseEntity<Void> = eventController.delete(sampleEvent.id)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

        verify(eventService).delete(sampleEvent.id)
    }

}
