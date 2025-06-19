package com.ozzz7y.opencalendar.event

import com.ozzz7y.opencalendar.domain.calendar.Calendar
import com.ozzz7y.opencalendar.domain.calendar.CalendarRepository
import com.ozzz7y.opencalendar.domain.category.Category
import com.ozzz7y.opencalendar.domain.category.CategoryColorHelper
import com.ozzz7y.opencalendar.domain.category.CategoryRepository
import com.ozzz7y.opencalendar.domain.event.*
import com.ozzz7y.opencalendar.domain.user.UserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
internal class EventServiceTest {

    @Mock
    private lateinit var _eventRepository: EventRepository

    @Mock
    private lateinit var _calendarRepository: CalendarRepository

    @Mock
    private lateinit var _categoryRepository: CategoryRepository

    @Mock
    private lateinit var _userService: UserService

    private lateinit var _service: EventService

    private lateinit var _calendar: Calendar

    private lateinit var _category: Category

    private lateinit var _event: Event

    private lateinit var _eventList: List<Event>

    private lateinit var _userId: UUID

    private lateinit var _now: LocalDateTime

    @BeforeEach
    fun setUp() {
        _userId = UUID.randomUUID()
        _now = LocalDateTime.now()

        _service = EventService(
            _eventRepository,
            _calendarRepository,
            _categoryRepository,
            _userService
        )

        _calendar = Calendar(
            id = UUID.randomUUID(),
            name = "Test",
            emoji = "🟢",
            _userId
        )
        _category = Category(
            id = UUID.randomUUID(),
            name = "Test",
            color = CategoryColorHelper.DEFAULT_COLOR,
            userId = _userId
        )
        _event = Event(
            id = UUID.randomUUID(),
            name = "Test",
            description = "Test",
            startDate = _now,
            endDate = _now.plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendar = _calendar,
            category = _category
        )

        _eventList = listOf(_event, _event.copy(), _event.copy())
    }

    @Test
    fun `should return created event`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_calendarRepository.findByIdAndUserId(id = _calendar.id, userId = _userId))
            .thenReturn(Optional.of(_calendar))
        whenever(_categoryRepository.findByIdAndUserId(id = _category.id, userId = _userId))
            .thenReturn(Optional.of(_category))
        whenever(_eventRepository.save(any<Event>())).thenReturn(_event)

        val result: EventDto = _service.create(dto = _event.toDto())

        assertEquals(_event.toDto(), result)

        verify(_eventRepository).save(any())
    }

    @Test
    fun `should throw error when creating event with missing calendar`() {
        whenever(_userService.getCurrentUserId())
            .thenReturn(_userId)
        whenever(_calendarRepository.findByIdAndUserId(id = _event.toDto().calendarId, userId = _userId))
            .thenReturn(Optional.empty())

        assertThrows<IllegalArgumentException> { _service.create(dto = _event.toDto()) }

        verify(_eventRepository, never()).save(any())
    }

    @Test
    fun `should return event by id`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_eventRepository.findByIdAndCalendarUserId(id = _event.id, userId = _userId))
            .thenReturn(Optional.of(_event))

        val result: EventDto = _service.getById(id = _event.id)

        assertEquals(_event.toDto(), result)

        verify(_eventRepository).findByIdAndCalendarUserId(id = _event.id, userId = _userId)
    }

    @Test
    fun `should throw error when event id not found`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_eventRepository.findByIdAndCalendarUserId(id = _event.id, userId = _userId))
            .thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> { _service.getById(id = _event.id) }

        verify(_eventRepository).findByIdAndCalendarUserId(id = _event.id, userId = _userId)
    }

    @Test
    fun `should return all events`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_eventRepository.findAllByCalendarUserId(userId = _userId)).thenReturn(_eventList)

        val result: List<EventDto> = _service.getAll()

        assertEquals(_eventList.map { it.toDto() }, result)

        verify(_eventRepository).findAllByCalendarUserId(_userId)
    }

    @Test
    fun `should return events by calendar id`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_eventRepository.findAllByCalendarIdAndCalendarUserId(calendarId = _calendar.id, userId = _userId))
            .thenReturn(_eventList)

        val result: List<EventDto> = _service.getAllByCalendarId(calendarId = _calendar.id)

        assertEquals(_eventList.map { it.toDto() }, result)

        verify(_eventRepository).findAllByCalendarIdAndCalendarUserId(calendarId = _calendar.id, userId = _userId)
    }

    @Test
    fun `should return events by category id`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_eventRepository.findAllByCategoryIdAndCalendarUserId(categoryId = _category.id, userId = _userId))
            .thenReturn(_eventList)

        val result: List<EventDto> = _service.getAllByCategoryId(categoryId = _category.id)

        assertEquals(_eventList.map { it.toDto() }, result)

        verify(_eventRepository).findAllByCategoryIdAndCalendarUserId(categoryId = _category.id, userId = _userId)
    }

    @Test
    fun `should return filtered events`() {
        val filter = EventFilterDto(
            name = "Test",
            description = null,
            dateFrom = _now.minusDays(1),
            dateTo = _now.plusDays(1),
            recurringPattern = null,
            calendarId = null,
            categoryId = null
        )

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(
            _eventRepository.filter(
                userId = _userId,
                name = filter.name,
                description = filter.description,
                dateFrom = filter.dateFrom,
                dateTo = filter.dateTo,
                recurringPattern = filter.recurringPattern,
                calendarId = filter.calendarId,
                categoryId = filter.categoryId
            )
        ).thenReturn(_eventList)

        val result: List<EventDto> = _service.filter(filter = filter)

        assertEquals(_eventList.map { it.toDto() }, result)
        verify(_eventRepository).filter(
            userId = _userId,
            name = filter.name,
            description = filter.description,
            dateFrom = filter.dateFrom,
            dateTo = filter.dateTo,
            recurringPattern = filter.recurringPattern,
            calendarId = filter.calendarId,
            categoryId = filter.categoryId
        )
    }

    @Test
    fun `should return updated event`() {
        val dto: EventDto = _event.toDto().copy(name = "Updated")

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_eventRepository.findByIdAndCalendarUserId(id = _event.id, userId = _userId))
            .thenReturn(Optional.of(_event))
        whenever(_calendarRepository.findByIdAndUserId(id = dto.calendarId, userId = _userId))
            .thenReturn(Optional.of(_calendar))
        whenever(_categoryRepository.findByIdAndUserId(id = dto.categoryId!!, userId = _userId))
            .thenReturn(Optional.of(_category))
        whenever(_eventRepository.save(any<Event>())).thenAnswer { it.arguments[0] as Event }

        val result: EventDto = _service.update(id = _event.id, dto = dto)

        assertEquals(dto, result)

        verify(_eventRepository).save(any())
    }

    @Test
    fun `should throw error when updating non existing event`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_eventRepository.findByIdAndCalendarUserId(id = _event.id, userId = _userId))
            .thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> { _service.update(id = _event.id, dto = _event.toDto()) }

        verify(_eventRepository, never()).save(any())
    }

    @Test
    fun `should delete event`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_eventRepository.findByIdAndCalendarUserId(id = _event.id, userId = _userId))
            .thenReturn(Optional.of(_event))
        doNothing().whenever(_eventRepository).delete(_event)

        _service.delete(id = _event.id)

        verify(_eventRepository).findByIdAndCalendarUserId(id = _event.id, userId = _userId)
        verify(_eventRepository).delete(_event)
    }

    @Test
    fun `should delete all events by calendar id`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_eventRepository.findAllByCalendarIdAndCalendarUserId(calendarId = _calendar.id, userId = _userId))
            .thenReturn(_eventList)
        doNothing().whenever(_eventRepository).deleteAll(_eventList)

        _service.deleteAllByCalendarId(calendarId = _calendar.id)

        verify(_eventRepository).findAllByCalendarIdAndCalendarUserId(calendarId = _calendar.id, userId = _userId)
        verify(_eventRepository).deleteAll(_eventList)
    }

    @Test
    fun `should clear category for all events by category id`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_eventRepository.findAllByCategoryIdAndCalendarUserId(categoryId = _category.id, userId = _userId))
            .thenReturn(_eventList)
        whenever(_eventRepository.save(any<Event>())).thenAnswer { it.arguments[0] as Event }

        _service.removeCategoryByCategoryId(categoryId = _category.id)

        verify(_eventRepository).findAllByCategoryIdAndCalendarUserId(categoryId = _category.id, userId = _userId)
        verify(_eventRepository, times(_eventList.size)).save(argThat { this.category == null })
    }

}
