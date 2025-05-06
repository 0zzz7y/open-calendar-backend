/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.event

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.domain.event.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime
import java.util.*

/**
 * Unit tests for the `EventController` class.
 * Verifies the behavior of the controller's endpoints using mocked dependencies.
 */
@ExtendWith(MockitoExtension::class)
internal class EventControllerTest {

    /**
     * Mocked instance of `EventService` for simulating event-related operations.
     */
    @Mock
    private lateinit var _eventService: EventService

    /**
     * Injected instance of `EventController` with mocked dependencies.
     */
    @InjectMocks
    private lateinit var _controller: EventController

    /**
     * Pageable instance for simulating pagination in tests.
     */
    private lateinit var _pageable: Pageable

    /**
     * Sample UUID used for testing.
     */
    private lateinit var _sampleId: UUID

    /**
     * Sample `EventDto` instance used in tests.
     */
    private lateinit var _sampleDto: EventDto

    /**
     * Current timestamp used for creating sample data.
     */
    private val _now: LocalDateTime = LocalDateTime.now()

    /**
     * Sets up the test environment before each test.
     * Initializes `Pageable`, sample UUID, and sample `EventDto`.
     */
    @BeforeEach
    fun setUp() {
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
        _sampleId = UUID.randomUUID()
        _sampleDto = EventDto(
            id = _sampleId,
            title = "Team Meeting",
            description = "Quarterly strategy discussion",
            startDate = _now,
            endDate = _now.plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendarId = UUID.randomUUID(),
            categoryId = UUID.randomUUID()
        )
    }

    /**
     * Tests the creation of an event.
     * Verifies that the endpoint returns a 201 Created status and the created event.
     */
    @Test
    fun `should create event with status code 201 Created`() {
        whenever(_eventService.create(eq(_sampleDto))).thenReturn(_sampleDto)

        val response: ResponseEntity<EventDto> = _controller.create(dto = _sampleDto)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(_sampleDto, response.body)
        verify(_eventService).create(eq(_sampleDto))
    }

    /**
     * Tests retrieving all events.
     * Verifies that the endpoint returns a 200 OK status and a list of events.
     */
    @Test
    fun `should return all events with status code 200 OK`() {
        val dto1 = _sampleDto.copy(id = UUID.randomUUID(), title = "Product Launch")
        val dto2 = _sampleDto.copy(id = UUID.randomUUID(), title = "Team Retrospective")
        whenever(_eventService.getAll()).thenReturn(listOf(dto1, dto2))

        val response: ResponseEntity<Page<EventDto>> =
            _controller.getAll(pageable = _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(2L, response.body?.totalElements)
        val titles = response.body?.content?.map { it.title } ?: emptyList()
        assertTrue(titles.containsAll(listOf("Product Launch", "Team Retrospective")))
        verify(_eventService).getAll()
    }

    /**
     * Tests retrieving an event by its ID.
     * Verifies that the endpoint returns a 200 OK status and the requested event.
     */
    @Test
    fun `should return event by id with status code 200 OK`() {
        whenever(_eventService.getById(id = _sampleId)).thenReturn(_sampleDto)

        val response: ResponseEntity<EventDto> = _controller.getById(id = _sampleId)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(_sampleDto, response.body)
        verify(_eventService).getById(id = _sampleId)
    }

    /**
     * Tests filtering events based on criteria.
     * Verifies that the endpoint returns a 200 OK status and a list of filtered events.
     */
    @Test
    fun `should return filtered events with status code 200 OK`() {
        val dateFrom = _now.minusDays(1).toString()
        val dateTo = _now.plusDays(1).toString()
        val pattern = RecurringPattern.DAILY.name
        val calId = _sampleDto.calendarId
        val catId = _sampleDto.categoryId
        val filteredDto = _sampleDto.copy(id = UUID.randomUUID(), title = "Strategy Session")

        whenever(_eventService.filter(filter = any<EventFilterDto>())).thenReturn(listOf(filteredDto))

        val response: ResponseEntity<Page<EventDto>> = _controller.filter(
            title = "Strategy",
            description = "discussion",
            dateFrom = dateFrom,
            dateTo = dateTo,
            recurringPattern = pattern,
            calendarId = calId,
            categoryId = catId,
            pageable = _pageable
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1L, response.body?.totalElements)
        assertEquals("Strategy Session", response.body?.content?.first()?.title)
        verify(_eventService).filter(filter = any<EventFilterDto>())
    }

    /**
     * Tests updating an event.
     * Verifies that the endpoint returns a 200 OK status and the updated event.
     */
    @Test
    fun `should update event with status code 200 OK`() {
        val updatedDto = _sampleDto.copy(title = "Planning Review")
        whenever(_eventService.update(id = _sampleId, dto = _sampleDto)).thenReturn(updatedDto)

        val response: ResponseEntity<EventDto> = _controller.update(id = _sampleId, dto = _sampleDto)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updatedDto, response.body)
        verify(_eventService).update(id = _sampleId, dto = _sampleDto)
    }

    /**
     * Tests deleting an event.
     * Verifies that the endpoint returns a 204 No Content status.
     */
    @Test
    fun `should delete event with status code 204 No Content`() {
        doNothing().whenever(_eventService).delete(id = _sampleId)

        val response: ResponseEntity<Void> = _controller.delete(id = _sampleId)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        verify(_eventService).delete(id = _sampleId)
    }

}
