package com.tomaszwnuk.opencalendar.event

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.calendar.Calendar
import com.tomaszwnuk.opencalendar.calendar.CalendarRepository
import com.tomaszwnuk.opencalendar.category.Category
import com.tomaszwnuk.opencalendar.category.CategoryColors
import com.tomaszwnuk.opencalendar.category.CategoryRepository
import com.tomaszwnuk.opencalendar.domain.RecurringPattern
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
import kotlin.test.assertEquals

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

    private lateinit var _sampleDto: EventDto

    @BeforeEach
    fun setup() {
        _sampleCalendar = Calendar(
            id = UUID.randomUUID(),
            name = "Work",
            emoji = "💼"
        )
        _sampleCategory = Category(
            id = UUID.randomUUID(),
            name = "Meeting",
            color = CategoryColors.toHex(Color.GREEN),
        )
        _sampleEvent = Event(
            id = UUID.randomUUID(),
            name = "Daily Standup",
            description = "Team sync",
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().plusHours(1),
            recurringPattern = RecurringPattern.DAILY,
            calendar = _sampleCalendar,
            category = _sampleCategory
        )
        _sampleDto = _sampleEvent.toDto()
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created event`() {
        whenever(_calendarRepository.findById(_sampleDto.calendarId)).thenReturn(Optional.of(_sampleCalendar))
        whenever(_categoryRepository.findById(_sampleDto.categoryId!!)).thenReturn(Optional.of(_sampleCategory))
        doReturn(_sampleEvent).whenever(_eventRepository).save(any())
        val result: Event = _eventService.create(_sampleDto)

        assertEquals(_sampleEvent.id, result.id)
        verify(_eventRepository).save(any())
    }

    @Test
    fun `should return paginated list of events`() {
        val events: List<Event> = listOf(_sampleEvent, _sampleEvent, _sampleEvent)

        whenever(_eventRepository.findAll(_pageable)).thenReturn(PageImpl(events))
        val result = _eventService.getAll(_pageable)

        assertEquals(events.size, result.totalElements.toInt())
        verify(_eventRepository).findAll(_pageable)
    }

    @Test
    fun `should return event by id`() {
        val id: UUID = _sampleEvent.id

        whenever(_eventRepository.findById(id)).thenReturn(Optional.of(_sampleEvent))
        val result: Event = _eventService.getById(id)

        assertEquals(_sampleEvent.name, result.name)
        verify(_eventRepository).findById(id)
    }

    @Test
    fun `should return filtered events`() {
        val filter = EventFilterDto(name = "Event")
        val events: List<Event> = listOf(_sampleEvent, _sampleEvent, _sampleEvent)

        whenever(
            _eventRepository.filter(
                eq(filter.name),
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
        verify(_eventRepository).filter(
            eq(filter.name),
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
        val updated: Event = _sampleEvent.copy(name = "Updated Event")

        whenever(_eventRepository.findById(id)).thenReturn(Optional.of(_sampleEvent))
        whenever(_calendarRepository.findById(_sampleDto.calendarId)).thenReturn(Optional.of(_sampleCalendar))
        whenever(_categoryRepository.findById(_sampleDto.categoryId!!)).thenReturn(Optional.of(_sampleCategory))
        doReturn(updated).whenever(_eventRepository).save(any())
        val result: Event = _eventService.update(id, _sampleDto)

        assertEquals(updated.name, result.name)
        verify(_eventRepository).save(any())
    }

    @Test
    fun `should delete event`() {
        val id: UUID = _sampleEvent.id

        whenever(_eventRepository.findById(id)).thenReturn(Optional.of(_sampleEvent))
        doNothing().whenever(_eventRepository).delete(_sampleEvent)
        _eventService.delete(id)

        verify(_eventRepository).delete(_sampleEvent)
    }

}
