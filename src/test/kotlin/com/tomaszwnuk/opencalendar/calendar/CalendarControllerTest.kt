/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.calendar

import com.fasterxml.jackson.databind.ObjectMapper
import com.tomaszwnuk.opencalendar.TestConstants
import com.tomaszwnuk.opencalendar.domain.calendar.CalendarController
import com.tomaszwnuk.opencalendar.domain.calendar.CalendarDto
import com.tomaszwnuk.opencalendar.domain.calendar.CalendarFilterDto
import com.tomaszwnuk.opencalendar.domain.calendar.CalendarService
import com.tomaszwnuk.opencalendar.domain.event.EventService
import com.tomaszwnuk.opencalendar.domain.note.NoteService
import com.tomaszwnuk.opencalendar.domain.task.TaskService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*

/**
 * Unit tests for the `CalendarController` class.
 * Verifies the behavior of the controller's endpoints using mocked dependencies.
 */
@ExtendWith(MockitoExtension::class)
internal class CalendarControllerTest {

    /**
     * Mocked instance of `CalendarService` for simulating calendar-related operations.
     */
    @Mock
    private lateinit var _calendarService: CalendarService

    /**
     * Mocked instance of `EventService` for simulating event-related operations.
     */
    @Mock
    private lateinit var _eventService: EventService

    /**
     * Mocked instance of `TaskService` for simulating task-related operations.
     */
    @Mock
    private lateinit var _taskService: TaskService

    /**
     * Mocked instance of `NoteService` for simulating note-related operations.
     */
    @Mock
    private lateinit var _noteService: NoteService

    /**
     * Injected instance of `CalendarController` with mocked dependencies.
     */
    @InjectMocks
    private lateinit var _controller: CalendarController

    /**
     * MockMvc instance for simulating HTTP requests to the controller.
     */
    private lateinit var _mockMvc: MockMvc

    /**
     * ObjectMapper instance for serializing and deserializing JSON.
     */
    private lateinit var _objectMapper: ObjectMapper

    /**
     * Sets up the test environment before each test.
     * Initializes `MockMvc` and `ObjectMapper`.
     */
    @BeforeEach
    fun setUp() {
        _objectMapper = ObjectMapper()
        _mockMvc = MockMvcBuilders
            .standaloneSetup(_controller)
            .setCustomArgumentResolvers(PageableHandlerMethodArgumentResolver())
            .build()
    }

    /**
     * Tests the creation of a calendar.
     * Verifies that the endpoint returns a 201 Created status and the created calendar.
     */
    @Test
    fun `should create calendar with status code 201 Created`() {
        val dto = CalendarDto(id = null, title = "Calendar", emoji = "🟢")
        val created = dto.copy(id = UUID.randomUUID())

        whenever(_calendarService.create(dto = dto)).thenReturn(created)

        _mockMvc.perform(
            post("/calendars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(_objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isCreated)
            .andExpect(content().json(_objectMapper.writeValueAsString(created)))
    }

    /**
     * Tests retrieving all calendars.
     * Verifies that the endpoint returns a 200 OK status and a list of calendars.
     */
    @Test
    fun `should return all calendars with status code 200 OK`() {
        val dto1 = CalendarDto(id = UUID.randomUUID(), title = "Work Calendar", emoji = "🟢")
        val dto2 = CalendarDto(id = UUID.randomUUID(), title = "Personal Calendar", emoji = "🔵")
        whenever(_calendarService.getAll()).thenReturn(listOf(dto1, dto2))

        _mockMvc.perform(
            get("/calendars")
                .param("page", TestConstants.PAGEABLE_PAGE_NUMBER.toString())
                .param("size", TestConstants.PAGEABLE_PAGE_SIZE.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[0].title").value("Work Calendar"))
            .andExpect(jsonPath("$.content[1].emoji").value("🔵"))
    }

    /**
     * Tests retrieving a calendar by its ID.
     * Verifies that the endpoint returns a 200 OK status and the requested calendar.
     */
    @Test
    fun `should return calendar by id with status code 200 OK`() {
        val id = UUID.randomUUID()
        val dto = CalendarDto(id = id, title = "Team Calendar", emoji = "🟢")
        whenever(_calendarService.getById(id = id)).thenReturn(dto)

        _mockMvc.perform(get("/calendars/$id"))
            .andExpect(status().isOk)
            .andExpect(content().json(_objectMapper.writeValueAsString(dto)))
    }

    /**
     * Tests filtering calendars based on criteria.
     * Verifies that the endpoint returns a 200 OK status and a list of filtered calendars.
     */
    @Test
    fun `should return list of filtered calendars with status code 200 OK`() {
        val dto = CalendarDto(id = UUID.randomUUID(), title = "Project Calendar", emoji = "🟢")
        whenever(_calendarService.filter(filter = any<CalendarFilterDto>())).thenReturn(listOf(dto))

        _mockMvc.perform(
            get("/calendars/filter")
                .param("title", "Project Calendar")
                .param("emoji", "🟢")
                .param("page", TestConstants.PAGEABLE_PAGE_NUMBER.toString())
                .param("size", TestConstants.PAGEABLE_PAGE_SIZE.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].title").value("Project Calendar"))
    }

    /**
     * Tests updating a calendar.
     * Verifies that the endpoint returns a 200 OK status and the updated calendar.
     */
    @Test
    fun `should update calendar with status code 200 OK`() {
        val id = UUID.randomUUID()
        val dto = CalendarDto(id = id, title = "Updated Calendar", emoji = "🔴")
        whenever(_calendarService.update(id = id, dto = dto)).thenReturn(dto)

        _mockMvc.perform(
            put("/calendars/$id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(_objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isOk)
            .andExpect(content().json(_objectMapper.writeValueAsString(dto)))
    }

    /**
     * Tests deleting a calendar.
     * Verifies that the endpoint returns a 204 No Content status.
     */
    @Test
    fun `should delete calendar with status code 204 No Content`() {
        val id = UUID.randomUUID()
        doNothing().whenever(_eventService).deleteAllByCalendarId(calendarId = id)
        doNothing().whenever(_taskService).deleteAllByCalendarId(calendarId = id)
        doNothing().whenever(_noteService).deleteByCalendarId(calendarId = id)
        doNothing().whenever(_calendarService).delete(id)

        _mockMvc.perform(delete("/calendars/$id"))
            .andExpect(status().isNoContent)
    }

}
