package com.tomaszwnuk.opencalendar.event

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.calendar.Calendar
import com.tomaszwnuk.opencalendar.calendar.CalendarRepository
import com.tomaszwnuk.opencalendar.category.Category
import com.tomaszwnuk.opencalendar.category.CategoryColorHelper
import com.tomaszwnuk.opencalendar.category.CategoryRepository
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
import org.mockito.kotlin.*
import org.mockito.quality.Strictness
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.awt.Color
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EventServiceTest {

    @Mock
    private lateinit var eventRepository: EventRepository

    @Mock
    private lateinit var calendarRepository: CalendarRepository

    @Mock
    private lateinit var categoryRepository: CategoryRepository

    @InjectMocks
    private lateinit var eventService: EventService

    private lateinit var sampleCalendar: Calendar

    private lateinit var sampleCategory: Category

    private lateinit var pageable: Pageable

    private lateinit var sampleEvent: Event

    private lateinit var sampleEventDto: EventDto

    @BeforeEach
    fun setup() {
        sampleCalendar = Calendar(
            id = UUID.randomUUID(),
            title = "Work",
            emoji = "💼"
        )
        sampleCategory = Category(
            id = UUID.randomUUID(),
            title = "Meeting",
            color = CategoryColorHelper.toHex(Color.GREEN),
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
    fun `should return created event`() {
        whenever(calendarRepository.findById(sampleEventDto.calendarId)).thenReturn(Optional.of(sampleCalendar))
        whenever(categoryRepository.findById(sampleEventDto.categoryId!!)).thenReturn(Optional.of(sampleCategory))
        doReturn(sampleEvent).whenever(eventRepository).save(any())

        val result: Event = eventService.create(sampleEventDto)

        assertNotNull(result)
        assertEquals(sampleEvent.id, result.id)
        assertEquals(sampleEvent.title, result.title)
        assertEquals(sampleEvent.description, result.description)
        assertEquals(sampleEvent.startDate, result.startDate)
        assertEquals(sampleEvent.endDate, result.endDate)
        assertEquals(sampleEvent.recurringPattern, result.recurringPattern)

        verify(eventRepository).save(any())
    }

    @Test
    fun `should return paginated list of all events`() {
        val events: List<Event> = listOf(sampleEvent, sampleEvent.copy(), sampleEvent.copy())
        whenever(eventRepository.findAll(pageable)).thenReturn(PageImpl(events))

        val result: Page<Event> = eventService.getAll(pageable)

        assertEquals(events.size, result.totalElements.toInt())
        assertEquals(events.map { it.id }, result.content.map { it.id })
        assertEquals(events.map { it.title }, result.content.map { it.title })

        verify(eventRepository).findAll(pageable)
    }

    @Test
    fun `should return event by id`() {
        val id: UUID = sampleEvent.id
        whenever(eventRepository.findById(id)).thenReturn(Optional.of(sampleEvent))

        val result: Event = eventService.getById(id)

        assertNotNull(result)
        assertEquals(sampleEvent.id, result.id)
        assertEquals(sampleEvent.title, result.title)
        assertEquals(sampleEvent.description, result.description)

        verify(eventRepository).findById(id)
    }

    @Test
    fun `should return paginated list of filtered events`() {
        val filter = EventFilterDto(title = "Event")
        val events: List<Event> = listOf(sampleEvent, sampleEvent.copy(), sampleEvent.copy())

        whenever(
            eventRepository.filter(
                eq(filter.title),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(pageable)
            )
        ).thenReturn(PageImpl(events))

        val result: Page<Event> = eventService.filter(filter, pageable)

        assertEquals(events.size, result.totalElements.toInt())
        assertEquals(events.map { it.title }, result.content.map { it.title })

        verify(eventRepository).filter(
            eq(filter.title),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            eq(pageable)
        )
    }

    @Test
    fun `should return updated event`() {
        val id: UUID = sampleEvent.id
        val updatedEvent: Event = sampleEvent.copy(title = "Updated Event")

        whenever(eventRepository.findById(id)).thenReturn(Optional.of(sampleEvent))
        whenever(calendarRepository.findById(sampleEventDto.calendarId)).thenReturn(Optional.of(sampleCalendar))
        whenever(categoryRepository.findById(sampleEventDto.categoryId!!)).thenReturn(Optional.of(sampleCategory))
        doReturn(updatedEvent).whenever(eventRepository).save(any())

        val result: Event = eventService.update(id, sampleEventDto)

        assertNotNull(result)
        assertEquals(updatedEvent.id, result.id)
        assertEquals("Updated Event", result.title)
        assertEquals(updatedEvent.description, result.description)

        verify(eventRepository).save(any())
    }

    @Test
    fun `should delete event by id`() {
        val id: UUID = sampleEvent.id
        whenever(eventRepository.findById(id)).thenReturn(Optional.of(sampleEvent))
        doNothing().whenever(eventRepository).delete(sampleEvent)

        eventService.delete(id)

        verify(eventRepository).delete(sampleEvent)
    }

}
