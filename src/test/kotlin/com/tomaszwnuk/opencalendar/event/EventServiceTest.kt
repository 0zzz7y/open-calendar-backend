package com.tomaszwnuk.opencalendar.event

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.calendar.CalendarRepository
import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.category.CategoryColorHelper
import com.tomaszwnuk.opencalendar.domain.category.CategoryRepository
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
    private lateinit var _eventRepository: EventRepository

    @Mock
    private lateinit var _calendarRepository: CalendarRepository

    @Mock
    private lateinit var _categoryRepository: CategoryRepository

    @InjectMocks
    private lateinit var _eventService: EventService

    private lateinit var _sampleCalendar: Calendar

    private lateinit var _sampleCategory: Category

    private lateinit var _pageable: Pageable

    private lateinit var _sampleEvent: Event

    private lateinit var _sampleEventDto: EventDto

    @BeforeEach
    fun setup() {
        _sampleCalendar = Calendar(
            id = UUID.randomUUID(),
            title = "Work",
            emoji = "💼"
        )
        _sampleCategory = Category(
            id = UUID.randomUUID(),
            title = "Meeting",
            color = CategoryColorHelper.toHex(Color.GREEN),
        )
        _sampleEvent = Event(
            id = UUID.randomUUID(),
            title = "Daily Standup",
            description = "Team sync",
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().plusHours(1),
            recurringPattern = RecurringPattern.DAILY,
            calendar = _sampleCalendar,
            category = _sampleCategory
        )
        _sampleEventDto = _sampleEvent.toDto()
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created event`() {
        whenever(_calendarRepository.findById(_sampleEventDto.calendarId)).thenReturn(Optional.of(_sampleCalendar))
        whenever(_categoryRepository.findById(_sampleEventDto.categoryId!!)).thenReturn(Optional.of(_sampleCategory))
        doReturn(_sampleEvent).whenever(_eventRepository).save(any())

        val result: Event = _eventService.create(_sampleEventDto)

        assertNotNull(result)
        assertEquals(_sampleEvent.id, result.id)
        assertEquals(_sampleEvent.title, result.title)
        assertEquals(_sampleEvent.description, result.description)
        assertEquals(_sampleEvent.startDate, result.startDate)
        assertEquals(_sampleEvent.endDate, result.endDate)
        assertEquals(_sampleEvent.recurringPattern, result.recurringPattern)

        verify(_eventRepository).save(any())
    }

    @Test
    fun `should return paginated list of all events`() {
        val events: List<Event> = listOf(_sampleEvent, _sampleEvent.copy(), _sampleEvent.copy())
        whenever(_eventRepository.findAll(_pageable)).thenReturn(PageImpl(events))

        val result: Page<Event> = _eventService.getAll(_pageable)

        assertEquals(events.size, result.totalElements.toInt())
        assertEquals(events.map { it.id }, result.content.map { it.id })
        assertEquals(events.map { it.title }, result.content.map { it.title })

        verify(_eventRepository).findAll(_pageable)
    }

    @Test
    fun `should return event by id`() {
        val id: UUID = _sampleEvent.id
        whenever(_eventRepository.findById(id)).thenReturn(Optional.of(_sampleEvent))

        val result: Event = _eventService.getById(id)

        assertNotNull(result)
        assertEquals(_sampleEvent.id, result.id)
        assertEquals(_sampleEvent.title, result.title)
        assertEquals(_sampleEvent.description, result.description)

        verify(_eventRepository).findById(id)
    }

    @Test
    fun `should return paginated list of filtered events`() {
        val filter = EventFilterDto(title = "Event")
        val events: List<Event> = listOf(_sampleEvent, _sampleEvent.copy(), _sampleEvent.copy())

        whenever(
            _eventRepository.filter(
                eq(filter.title),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(_pageable)
            )
        ).thenReturn(PageImpl(events))

        val result: Page<Event> = _eventService.filter(filter, _pageable)

        assertEquals(events.size, result.totalElements.toInt())
        assertEquals(events.map { it.title }, result.content.map { it.title })

        verify(_eventRepository).filter(
            eq(filter.title),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            eq(_pageable)
        )
    }

    @Test
    fun `should return updated event`() {
        val id: UUID = _sampleEvent.id
        val updatedEvent: Event = _sampleEvent.copy(title = "Updated Event")

        whenever(_eventRepository.findById(id)).thenReturn(Optional.of(_sampleEvent))
        whenever(_calendarRepository.findById(_sampleEventDto.calendarId)).thenReturn(Optional.of(_sampleCalendar))
        whenever(_categoryRepository.findById(_sampleEventDto.categoryId!!)).thenReturn(Optional.of(_sampleCategory))
        doReturn(updatedEvent).whenever(_eventRepository).save(any())

        val result: Event = _eventService.update(id, _sampleEventDto)

        assertNotNull(result)
        assertEquals(updatedEvent.id, result.id)
        assertEquals("Updated Event", result.title)
        assertEquals(updatedEvent.description, result.description)

        verify(_eventRepository).save(any())
    }

    @Test
    fun `should delete event by id`() {
        val id: UUID = _sampleEvent.id
        whenever(_eventRepository.findById(id)).thenReturn(Optional.of(_sampleEvent))
        doNothing().whenever(_eventRepository).delete(_sampleEvent)

        _eventService.delete(id)

        verify(_eventRepository).delete(_sampleEvent)
    }

}
