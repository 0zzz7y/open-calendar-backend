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

@ExtendWith(MockitoExtension::class)
internal class EventServiceTest {

    @Mock
    private lateinit var _eventRepository: EventRepository

    @Mock
    private lateinit var _calendarRepository: CalendarRepository

    @Mock
    private lateinit var _categoryRepository: CategoryRepository

    private lateinit var _service: EventService

    private val sampleCalendar = Calendar(
        id = UUID.randomUUID(), name = "Work Calendar", emoji = "🟢"
    )

    private val sampleCategory = Category(
        id = UUID.randomUUID(), name = "Business", color = "#FF0000"
    )

    private val now: LocalDateTime = LocalDateTime.now()

    @BeforeEach
    fun setUp() {
        _service = EventService(_eventRepository, _calendarRepository, _categoryRepository)
    }

    @Test
    fun `should return created event`() {
        val dto = EventDto(
            name = "Team Meeting",
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

        val result = _service.create(dto = dto)

        assertNotNull(result.id)
        assertEquals(savedId, result.id)
        assertEquals("Team Meeting", result.name)
        assertEquals(sampleCalendar.id, result.calendarId)
        assertEquals(sampleCategory.id, result.categoryId)

        verify(_calendarRepository).findById(sampleCalendar.id)
        verify(_categoryRepository).findById(sampleCategory.id)
        verify(_eventRepository).save(argThat { name == "Team Meeting" && calendar.id == sampleCalendar.id })
    }

    @Test
    fun `should throw error when creating event with missing calendar`() {
        val dto = EventDto(
            name = "Product Launch",
            description = null,
            startDate = now,
            endDate = now.plusHours(2),
            recurringPattern = RecurringPattern.NONE,
            calendarId = UUID.randomUUID(),
            categoryId = null
        )
        whenever(_calendarRepository.findById(dto.calendarId)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            _service.create(dto = dto)
        }

        verify(_calendarRepository).findById(dto.calendarId)
        verify(_eventRepository, never()).save(any<Event>())
    }

    @Test
    fun `should return event by id`() {
        val id = UUID.randomUUID()
        val event = Event(
            id = id,
            name = "Client Call",
            description = "Discuss contract",
            startDate = now,
            endDate = now.plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendar = sampleCalendar,
            category = null
        )
        whenever(_eventRepository.findById(id)).thenReturn(Optional.of(event))

        val result = _service.getById(id = id)

        assertEquals(id, result.id)
        assertEquals("Client Call", result.name)

        verify(_eventRepository).findById(id)
    }

    @Test
    fun `should throw error when event id not found`() {
        val id = UUID.randomUUID()
        whenever(_eventRepository.findById(id)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            _service.getById(id = id)
        }

        verify(_eventRepository).findById(id)
    }

    @Test
    fun `should return all events`() {
        val launchEvent = Event(
            name = "Product Launch",
            description = "Launch new product",
            startDate = now,
            endDate = now.plusHours(2),
            calendar = sampleCalendar
        )
        val reviewEvent = Event(
            name = "Retrospective",
            description = "Team retrospective",
            startDate = now,
            endDate = now.plusHours(1),
            calendar = sampleCalendar
        )
        whenever(_eventRepository.findAll()).thenReturn(listOf(launchEvent, reviewEvent))

        val result = _service.getAll()

        assertEquals(2, result.size)
        assertTrue(result.any { it.name == "Product Launch" })
        assertTrue(result.any { it.name == "Retrospective" })

        verify(_eventRepository).findAll()
    }

    @Test
    fun `should return events by calendar id`() {
        val id = sampleCalendar.id
        val workshop = Event(
            name = "Workshop",
            description = "Skills workshop",
            startDate = now,
            endDate = now.plusHours(3),
            calendar = sampleCalendar
        )
        whenever(_eventRepository.findAllByCalendarId(calendarId = id)).thenReturn(listOf(workshop))

        val result = _service.getAllByCalendarId(calendarId = id)

        assertEquals(1, result.size)
        assertEquals("Workshop", result[0].name)

        verify(_eventRepository).findAllByCalendarId(calendarId = id)
    }

    @Test
    fun `should return events by category id`() {
        val id = sampleCategory.id
        val training = Event(
            name = "Training Session",
            description = "Employee training",
            startDate = now,
            endDate = now.plusHours(2),
            calendar = sampleCalendar,
            category = sampleCategory
        )
        whenever(_eventRepository.findAllByCategoryId(categoryId = id)).thenReturn(listOf(training))

        val result = _service.getAllByCategoryId(categoryId = id)

        assertEquals(1, result.size)
        assertEquals("Training Session", result[0].name)

        verify(_eventRepository).findAllByCategoryId(categoryId = id)
    }

    @Test
    fun `should return filtered events`() {
        val filter = EventFilterDto(
            name = "Strategy Meeting",
            description = null,
            dateFrom = null,
            dateTo = null,
            recurringPattern = null,
            calendarId = null,
            categoryId = null
        )
        val strategyEvent = Event(
            name = "Strategy Meeting",
            description = "Planning for Q3",
            startDate = now,
            endDate = now.plusHours(1),
            calendar = sampleCalendar
        )
        whenever(
            _eventRepository.filter(
                name = "Strategy Meeting",
                description = null,
                dateFrom = null,
                dateTo = null,
                recurringPattern = null,
                calendarId = null,
                categoryId = null
            )
        ).thenReturn(listOf(strategyEvent))

        val result = _service.filter(filter = filter)

        assertEquals(1, result.size)
        assertEquals("Strategy Meeting", result[0].name)

        verify(_eventRepository).filter(
            name = "Strategy Meeting",
            description = null,
            dateFrom = null,
            dateTo = null,
            recurringPattern = null,
            calendarId = null,
            categoryId = null
        )
    }

    @Test
    fun `should return updated event`() {
        val id = UUID.randomUUID()
        val existing = Event(
            id = id,
            name = "Planning Session",
            description = "Initial planning",
            startDate = now,
            endDate = now.plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendar = sampleCalendar,
            category = sampleCategory
        )
        val dto = existing.toDto().copy(name = "Planning Review")
        whenever(_eventRepository.findById(id)).thenReturn(Optional.of(existing))
        whenever(_calendarRepository.findById(dto.calendarId)).thenReturn(Optional.of(sampleCalendar))
        whenever(_categoryRepository.findById(dto.categoryId!!)).thenReturn(Optional.of(sampleCategory))
        whenever(_eventRepository.save(any<Event>())).thenAnswer { it.getArgument<Event>(0) }

        val result = _service.update(id = id, dto = dto)

        assertEquals("Planning Review", result.name)
        verify(_eventRepository).findById(id)
        verify(_eventRepository).save(argThat { name == "Planning Review" })
    }

    @Test
    fun `should throw error when updating non existing event`() {
        val id = UUID.randomUUID()
        val dto = EventDto(
            id = null,
            name = "Resume Meeting",
            startDate = now,
            endDate = now.plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendarId = sampleCalendar.id
        )
        whenever(_eventRepository.findById(id)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            _service.update(id = id, dto = dto)
        }

        verify(_eventRepository).findById(id)
    }

    @Test
    fun `should delete event when exists`() {
        val id = UUID.randomUUID()
        val deadlineEvent = Event(
            name = "Project Deadline",
            description = "Submit final report",
            startDate = now,
            endDate = now.plusHours(1),
            calendar = sampleCalendar
        )
        whenever(_eventRepository.findById(id)).thenReturn(Optional.of(deadlineEvent))
        doNothing().whenever(_eventRepository).delete(deadlineEvent)

        _service.delete(id = id)

        verify(_eventRepository).findById(id)
        verify(_eventRepository).delete(deadlineEvent)
    }

    @Test
    fun `should delete all events by calendar id`() {
        val calendarId = sampleCalendar.id
        val planning = Event(
            name = "Sprint Planning",
            description = "Plan next sprint",
            startDate = now,
            endDate = now.plusHours(2),
            calendar = sampleCalendar
        )
        val planningFollowUp = planning.copy(id = UUID.randomUUID())
        whenever(_eventRepository.findAllByCalendarId(calendarId = calendarId)).thenReturn(
            listOf(
                planning,
                planningFollowUp
            )
        )
        doNothing().whenever(_eventRepository).deleteAll(listOf(planning, planningFollowUp))

        _service.deleteAllByCalendarId(calendarId = calendarId)

        verify(_eventRepository).findAllByCalendarId(calendarId = calendarId)
        verify(_eventRepository).deleteAll(listOf(planning, planningFollowUp))
    }

    @Test
    fun `should delete all events by category id`() {
        val categoryId = sampleCategory.id
        val budgetReview = Event(
            name = "Budget Review",
            description = "Review quarterly budget",
            startDate = now,
            endDate = now.plusHours(1),
            calendar = sampleCalendar,
            category = sampleCategory
        )
        val budgetFollowUp = budgetReview.copy(id = UUID.randomUUID())
        whenever(_eventRepository.findAllByCategoryId(categoryId = categoryId)).thenReturn(
            listOf(
                budgetReview,
                budgetFollowUp
            )
        )
        whenever(_eventRepository.save(any<Event>())).thenAnswer { it.getArgument<Event>(0) }

        _service.removeCategoryByCategoryId(categoryId = categoryId)

        verify(_eventRepository).findAllByCategoryId(categoryId = categoryId)
        verify(_eventRepository).save(argThat { id == budgetReview.id && category == null })
        verify(_eventRepository).save(argThat { id == budgetFollowUp.id && category == null })
    }

}
