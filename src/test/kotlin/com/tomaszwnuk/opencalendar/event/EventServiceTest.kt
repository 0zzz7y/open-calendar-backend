/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.event

import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.calendar.CalendarRepository
import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.category.CategoryRepository
import com.tomaszwnuk.opencalendar.domain.event.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.time.LocalDateTime
import java.util.*

/**
 * Unit tests for the `EventService` class.
 * Verifies the behavior of the service methods using mocked dependencies.
 */
@ExtendWith(MockitoExtension::class)
internal class EventServiceTest {

    /**
     * Mocked instance of `EventRepository` for simulating event-related database operations.
     */
    @Mock
    private lateinit var _eventRepository: EventRepository

    /**
     * Mocked instance of `CalendarRepository` for simulating calendar-related database operations.
     */
    @Mock
    private lateinit var _calendarRepository: CalendarRepository

    /**
     * Mocked instance of `CategoryRepository` for simulating category-related database operations.
     */
    @Mock
    private lateinit var _categoryRepository: CategoryRepository

    /**
     * Instance of `EventService` under test.
     */
    private lateinit var _service: EventService

    /**
     * Sample `Calendar` instance used in tests.
     */
    private val sampleCalendar = Calendar(
        id = UUID.randomUUID(), title = "Work Calendar", emoji = "🟢"
    )

    /**
     * Sample `Category` instance used in tests.
     */
    private val sampleCategory = Category(
        id = UUID.randomUUID(), title = "Business", color = "#FF0000"
    )

    /**
     * Current timestamp used for creating sample data.
     */
    private val now: LocalDateTime = LocalDateTime.now()

    /**
     * Sets up the test environment before each test.
     * Initializes the `EventService` with mocked repositories.
     */
    @BeforeEach
    fun setUp() {
        _service = EventService(_eventRepository, _calendarRepository, _categoryRepository)
    }

    /**
     * Tests the creation of an event.
     * Verifies that the service returns the created event with a generated ID.
     */
    @Test
    fun `should return created event`() {
        val dto = EventDto(
            title = "Team Meeting",
            description = "Quarterly strategy discussion",
            startDate = now,
            endDate = now.plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendarId = sampleCalendar.id,
            categoryId = sampleCategory.id
        )
        val savedId = UUID.randomUUID()

        whenever(_calendarRepository.findById(sampleCalendar.id)).thenReturn(Optional.of(sampleCalendar))
        whenever(_categoryRepository.findById(sampleCategory.id)).thenReturn(Optional.of(sampleCategory))
        whenever(_eventRepository.save(any<Event>())).thenAnswer { invocation ->
            val arg = invocation.getArgument<Event>(0)
            arg.copy(id = savedId)
        }

        val result = _service.create(dto)

        assertNotNull(result.id)
        assertEquals(savedId, result.id)
        assertEquals("Team Meeting", result.title)
        assertEquals(sampleCalendar.id, result.calendarId)
        assertEquals(sampleCategory.id, result.categoryId)

        verify(_calendarRepository).findById(sampleCalendar.id)
        verify(_categoryRepository).findById(sampleCategory.id)
        verify(_eventRepository).save(argThat { title == "Team Meeting" && calendar.id == sampleCalendar.id })
    }

    /**
     * Tests the creation of an event with a missing calendar.
     * Verifies that the service throws a `NoSuchElementException`.
     */
    @Test
    fun `should throw error when creating event with missing calendar`() {
        val dto = EventDto(
            title = "Product Launch",
            description = null,
            startDate = now,
            endDate = now.plusHours(2),
            recurringPattern = RecurringPattern.NONE,
            calendarId = UUID.randomUUID(),
            categoryId = null
        )
        whenever(_calendarRepository.findById(dto.calendarId)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            _service.create(dto)
        }

        verify(_calendarRepository).findById(dto.calendarId)
        verify(_eventRepository, never()).save(any<Event>())
    }

    /**
     * Tests retrieving an event by its ID.
     * Verifies that the service returns the correct event.
     */
    @Test
    fun `should return event by id`() {
        val id = UUID.randomUUID()
        val event = Event(
            id = id,
            title = "Client Call",
            description = "Discuss contract",
            startDate = now,
            endDate = now.plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendar = sampleCalendar,
            category = null
        )
        whenever(_eventRepository.findById(id)).thenReturn(Optional.of(event))

        val result = _service.getById(id)

        assertEquals(id, result.id)
        assertEquals("Client Call", result.title)

        verify(_eventRepository).findById(id)
    }

    /**
     * Tests retrieving an event by a non-existent ID.
     * Verifies that the service throws a `NoSuchElementException`.
     */
    @Test
    fun `should throw error when event id not found`() {
        val id = UUID.randomUUID()
        whenever(_eventRepository.findById(id)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            _service.getById(id)
        }

        verify(_eventRepository).findById(id)
    }

    /**
     * Tests retrieving all events.
     * Verifies that the service returns a list of all events.
     */
    @Test
    fun `should return all events`() {
        val launchEvent = Event(
            title = "Product Launch",
            description = "Launch new product",
            startDate = now,
            endDate = now.plusHours(2),
            calendar = sampleCalendar
        )
        val reviewEvent = Event(
            title = "Retrospective",
            description = "Team retrospective",
            startDate = now,
            endDate = now.plusHours(1),
            calendar = sampleCalendar
        )
        whenever(_eventRepository.findAll()).thenReturn(listOf(launchEvent, reviewEvent))

        val result = _service.getAll()

        assertEquals(2, result.size)
        assertTrue(result.any { it.title == "Product Launch" })
        assertTrue(result.any { it.title == "Retrospective" })

        verify(_eventRepository).findAll()
    }

    /**
     * Tests retrieving events by calendar ID.
     * Verifies that the service returns a list of events associated with the specified calendar.
     */
    @Test
    fun `should return events by calendar id`() {
        val id = sampleCalendar.id
        val workshop = Event(
            title = "Workshop",
            description = "Skills workshop",
            startDate = now,
            endDate = now.plusHours(3),
            calendar = sampleCalendar
        )
        whenever(_eventRepository.findAllByCalendarId(id)).thenReturn(listOf(workshop))

        val result = _service.getAllByCalendarId(id)

        assertEquals(1, result.size)
        assertEquals("Workshop", result[0].title)

        verify(_eventRepository).findAllByCalendarId(id)
    }

    /**
     * Tests retrieving events by category ID.
     * Verifies that the service returns a list of events associated with the specified category.
     */
    @Test
    fun `should return events by category id`() {
        val id = sampleCategory.id
        val training = Event(
            title = "Training Session",
            description = "Employee training",
            startDate = now,
            endDate = now.plusHours(2),
            calendar = sampleCalendar,
            category = sampleCategory
        )
        whenever(_eventRepository.findAllByCategoryId(id)).thenReturn(listOf(training))

        val result = _service.getAllByCategoryId(id)

        assertEquals(1, result.size)
        assertEquals("Training Session", result[0].title)

        verify(_eventRepository).findAllByCategoryId(id)
    }

    /**
     * Tests filtering events based on criteria.
     * Verifies that the service returns a list of matching events.
     */
    @Test
    fun `should return filtered events`() {
        val filter = EventFilterDto(
            title = "Strategy Meeting",
            description = null,
            dateFrom = null,
            dateTo = null,
            recurringPattern = null,
            calendarId = null,
            categoryId = null
        )
        val strategyEvent = Event(
            title = "Strategy Meeting",
            description = "Planning for Q3",
            startDate = now,
            endDate = now.plusHours(1),
            calendar = sampleCalendar
        )
        whenever(
            _eventRepository.filter(
                "Strategy Meeting", null, null, null, null, null, null
            )
        ).thenReturn(listOf(strategyEvent))

        val result = _service.filter(filter)

        assertEquals(1, result.size)
        assertEquals("Strategy Meeting", result[0].title)

        verify(_eventRepository).filter("Strategy Meeting", null, null, null, null, null, null)
    }

    /**
     * Tests updating an event.
     * Verifies that the service updates the event and returns the updated entity.
     */
    @Test
    fun `should return updated event`() {
        val id = UUID.randomUUID()
        val existing = Event(
            id = id,
            title = "Planning Session",
            description = "Initial planning",
            startDate = now,
            endDate = now.plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendar = sampleCalendar,
            category = sampleCategory
        )
        val dto = existing.toDto().copy(title = "Planning Review")
        whenever(_eventRepository.findById(id)).thenReturn(Optional.of(existing))
        whenever(_calendarRepository.findById(dto.calendarId)).thenReturn(Optional.of(sampleCalendar))
        whenever(_categoryRepository.findById(dto.categoryId!!)).thenReturn(Optional.of(sampleCategory))
        whenever(_eventRepository.save(any<Event>())).thenAnswer { it.getArgument<Event>(0) }

        val result = _service.update(id, dto)

        assertEquals("Planning Review", result.title)
        verify(_eventRepository).findById(id)
        verify(_eventRepository).save(argThat { title == "Planning Review" })
    }

    /**
     * Tests updating a non-existent event.
     * Verifies that the service throws a `NoSuchElementException`.
     */
    @Test
    fun `should throw error when updating non existing event`() {
        val id = UUID.randomUUID()
        val dto = EventDto(
            id = null,
            title = "Resume Meeting",
            startDate = now,
            endDate = now.plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendarId = sampleCalendar.id
        )
        whenever(_eventRepository.findById(id)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            _service.update(id, dto)
        }

        verify(_eventRepository).findById(id)
    }

    /**
     * Tests deleting an event that exists.
     * Verifies that the service deletes the event.
     */
    @Test
    fun `should delete event when exists`() {
        val id = UUID.randomUUID()
        val deadlineEvent = Event(
            title = "Project Deadline",
            description = "Submit final report",
            startDate = now,
            endDate = now.plusHours(1),
            calendar = sampleCalendar
        )
        whenever(_eventRepository.findById(id)).thenReturn(Optional.of(deadlineEvent))
        doNothing().whenever(_eventRepository).delete(deadlineEvent)

        _service.delete(id)

        verify(_eventRepository).findById(id)
        verify(_eventRepository).delete(deadlineEvent)
    }

    /**
     * Tests deleting all events by calendar ID.
     * Verifies that the service deletes all events associated with the specified calendar.
     */
    @Test
    fun `should delete all events by calendar id`() {
        val calId = sampleCalendar.id
        val planning = Event(
            title = "Sprint Planning",
            description = "Plan next sprint",
            startDate = now,
            endDate = now.plusHours(2),
            calendar = sampleCalendar
        )
        val planningFollowUp = planning.copy(id = UUID.randomUUID())
        whenever(_eventRepository.findAllByCalendarId(calId)).thenReturn(listOf(planning, planningFollowUp))
        doNothing().whenever(_eventRepository).deleteAll(listOf(planning, planningFollowUp))

        _service.deleteAllByCalendarId(calId)

        verify(_eventRepository).findAllByCalendarId(calId)
        verify(_eventRepository).deleteAll(listOf(planning, planningFollowUp))
    }

    /**
     * Tests deleting all events by category ID.
     * Verifies that the service removes the category association from all events in the specified category.
     */
    @Test
    fun `should delete all events by category id`() {
        val catId = sampleCategory.id
        val budgetReview = Event(
            title = "Budget Review",
            description = "Review quarterly budget",
            startDate = now,
            endDate = now.plusHours(1),
            calendar = sampleCalendar,
            category = sampleCategory
        )
        val budgetFollowUp = budgetReview.copy(id = UUID.randomUUID())
        whenever(_eventRepository.findAllByCategoryId(catId)).thenReturn(listOf(budgetReview, budgetFollowUp))
        whenever(_eventRepository.save(any<Event>())).thenAnswer { it.getArgument<Event>(0) }

        _service.deleteAllByCategoryId(catId)

        verify(_eventRepository).findAllByCategoryId(catId)
        verify(_eventRepository).save(argThat { id == budgetReview.id && category == null })
        verify(_eventRepository).save(argThat { id == budgetFollowUp.id && category == null })
    }

}
