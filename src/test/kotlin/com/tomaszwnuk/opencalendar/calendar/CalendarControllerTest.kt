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

@ExtendWith(MockitoExtension::class)
internal class CalendarControllerTest {

    @Mock
    private lateinit var _calendarService: CalendarService

    @Mock
    private lateinit var _eventService: EventService

    @Mock
    private lateinit var _taskService: TaskService

    @Mock
    private lateinit var _noteService: NoteService

    @InjectMocks
    private lateinit var _controller: CalendarController

    private lateinit var _mockMvc: MockMvc

    private lateinit var _objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        _objectMapper = ObjectMapper()
        _mockMvc = MockMvcBuilders
            .standaloneSetup(_controller)
            .setCustomArgumentResolvers(PageableHandlerMethodArgumentResolver())
            .build()
    }

    @Test
    fun `should create calendar with status code 201 Created`() {
        val dto = CalendarDto(id = null, name = "Calendar", emoji = "🟢")
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

    @Test
    fun `should return all calendars with status code 200 OK`() {
        val dto1 = CalendarDto(id = UUID.randomUUID(), name = "Work Calendar", emoji = "🟢")
        val dto2 = CalendarDto(id = UUID.randomUUID(), name = "Personal Calendar", emoji = "🔵")
        whenever(_calendarService.getAll()).thenReturn(listOf(dto1, dto2))

        _mockMvc.perform(
            get("/calendars")
                .param("page", TestConstants.PAGEABLE_PAGE_NUMBER.toString())
                .param("size", TestConstants.PAGEABLE_PAGE_SIZE.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[0].name").value("Work Calendar"))
            .andExpect(jsonPath("$.content[1].emoji").value("🔵"))
    }

    @Test
    fun `should return calendar by id with status code 200 OK`() {
        val id = UUID.randomUUID()
        val dto = CalendarDto(id = id, name = "Team Calendar", emoji = "🟢")
        whenever(_calendarService.getById(id = id)).thenReturn(dto)

        _mockMvc.perform(get("/calendars/$id"))
            .andExpect(status().isOk)
            .andExpect(content().json(_objectMapper.writeValueAsString(dto)))
    }

    @Test
    fun `should return list of filtered calendars with status code 200 OK`() {
        val dto = CalendarDto(id = UUID.randomUUID(), name = "Project Calendar", emoji = "🟢")
        whenever(_calendarService.filter(filter = any<CalendarFilterDto>())).thenReturn(listOf(dto))

        _mockMvc.perform(
            get("/calendars/filter")
                .param("title", "Project Calendar")
                .param("emoji", "🟢")
                .param("page", TestConstants.PAGEABLE_PAGE_NUMBER.toString())
                .param("size", TestConstants.PAGEABLE_PAGE_SIZE.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].name").value("Project Calendar"))
    }

    @Test
    fun `should update calendar with status code 200 OK`() {
        val id = UUID.randomUUID()
        val dto = CalendarDto(id = id, name = "Updated Calendar", emoji = "🔴")
        whenever(_calendarService.update(id = id, dto = dto)).thenReturn(dto)

        _mockMvc.perform(
            put("/calendars/$id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(_objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isOk)
            .andExpect(content().json(_objectMapper.writeValueAsString(dto)))
    }

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
