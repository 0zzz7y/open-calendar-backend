/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.category

import com.fasterxml.jackson.databind.ObjectMapper
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.category.*
import com.tomaszwnuk.opencalendar.domain.event.EventDto
import com.tomaszwnuk.opencalendar.domain.event.EventService
import com.tomaszwnuk.opencalendar.domain.event.RecurringPattern
import com.tomaszwnuk.opencalendar.domain.note.NoteDto
import com.tomaszwnuk.opencalendar.domain.note.NoteService
import com.tomaszwnuk.opencalendar.domain.task.TaskDto
import com.tomaszwnuk.opencalendar.domain.task.TaskService
import com.tomaszwnuk.opencalendar.domain.task.TaskStatus
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.awt.Color
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
internal class CategoryControllerTest {

    @Mock
    private lateinit var _categoryService: CategoryService

    @Mock
    private lateinit var _eventService: EventService

    @Mock
    private lateinit var _taskService: TaskService

    @Mock
    private lateinit var _noteService: NoteService

    @InjectMocks
    private lateinit var _controller: CategoryController

    private lateinit var _mockMvc: MockMvc

    private lateinit var _objectMapper: ObjectMapper

    private lateinit var _sampleCategory: Category

    private lateinit var _sampleCategoryDto: CategoryDto

    private lateinit var _pageable: Pageable

    @BeforeEach
    fun setUp() {
        _sampleCategory = Category(
            id = UUID.randomUUID(),
            name = "Personal",
            color = CategoryColorHelper.toHex(Color.GREEN)
        )
        _sampleCategoryDto = _sampleCategory.toDto()
        _objectMapper = ObjectMapper()
        _pageable = PageRequest.of(
            PAGEABLE_PAGE_NUMBER,
            PAGEABLE_PAGE_SIZE
        )
        _mockMvc = MockMvcBuilders
            .standaloneSetup(_controller)
            .setCustomArgumentResolvers(PageableHandlerMethodArgumentResolver())
            .build()
    }

    @Test
    fun `should return created category with status code 201 Created`() {
        whenever(_categoryService.create(eq(_sampleCategoryDto))).thenReturn(_sampleCategoryDto)

        val response: ResponseEntity<CategoryDto> = _controller.create(_sampleCategoryDto)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(_sampleCategoryDto, response.body)

        verify(_categoryService).create(eq(_sampleCategoryDto))
    }

    @Test
    fun `should return paginated list of all categories with status code 200 OK`() {
        val categories = listOf(_sampleCategory, _sampleCategory.copy(), _sampleCategory.copy())
        whenever(_categoryService.getAll()).thenReturn(categories.map { it.toDto() })

        val response: ResponseEntity<Page<CategoryDto>> =
            _controller.getAll(pageable = _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(categories.size.toLong(), response.body?.totalElements)
        assertEquals(categories.map { it.id }, response.body?.content?.map { it.id })

        verify(_categoryService).getAll()
    }

    @Test
    fun `should return category by id with status code 200 OK`() {
        val id = _sampleCategory.id
        whenever(_categoryService.getById(id = id)).thenReturn(_sampleCategoryDto)

        val response: ResponseEntity<CategoryDto> = _controller.getById(id = id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(_sampleCategoryDto, response.body)

        verify(_categoryService).getById(id)
    }

    @Test
    fun `should return paginated list of category events with status code 200 OK`() {
        val id = _sampleCategory.id
        val now = LocalDateTime.now()
        val calendar = Calendar(name = "Calendar", emoji = "📅")
        val eventDto = EventDto(
            id = UUID.randomUUID(),
            name = "Event",
            description = "Desc",
            startDate = now,
            endDate = now.plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendarId = calendar.id
        )
        whenever(_eventService.getAllByCategoryId(categoryId = id)).thenReturn(listOf(eventDto))

        val response: ResponseEntity<Page<EventDto>> =
            _controller.getEvents(id = id, pageable = _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, response.body?.totalElements)
        assertEquals(eventDto.name, response.body?.content?.first()?.name)

        verify(_eventService).getAllByCategoryId(categoryId = id)
    }

    @Test
    fun `should return paginated list of category tasks with status code 200 OK`() {
        val id = _sampleCategory.id
        val calendar = Calendar(id = UUID.randomUUID(), name = "Calendar", emoji = "📅")
        val taskDto = TaskDto(
            id = UUID.randomUUID(),
            name = "Task",
            description = "Desc",
            status = TaskStatus.TODO,
            calendarId = calendar.id
        )
        whenever(_taskService.getAllByCategoryId(categoryId = id)).thenReturn(listOf(taskDto))

        val response: ResponseEntity<Page<TaskDto>> =
            _controller.getTasks(id = id, pageable = _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, response.body?.totalElements)
        assertEquals(taskDto.name, response.body?.content?.first()?.name)

        verify(_taskService).getAllByCategoryId(categoryId = id)
    }

    @Test
    fun `should return paginated list of category notes with status code 200 OK`() {
        val id = _sampleCategory.id
        val calendar = Calendar(id = UUID.randomUUID(), name = "Calendar", emoji = "📅")
        val noteDto = NoteDto(
            id = UUID.randomUUID(),
            name = "Note",
            description = "Desc",
            calendarId = calendar.id
        )
        whenever(_noteService.getAllByCategoryId(id)).thenReturn(listOf(noteDto))

        val response: ResponseEntity<Page<NoteDto>> =
            _controller.getNotes(id, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, response.body?.totalElements)
        assertEquals(noteDto.name, response.body?.content?.first()?.name)

        verify(_noteService).getAllByCategoryId(id)
    }

    @Test
    fun `should return combined list of items with status code 200 OK`() {
        val id = _sampleCategory.id
        val now = LocalDateTime.now()
        val calendar = Calendar(name = "Calendar", emoji = "📅")
        val eventDto = EventDto(
            id = UUID.randomUUID(),
            name = "Event",
            description = "",
            startDate = now,
            endDate = now,
            recurringPattern = RecurringPattern.NONE,
            calendarId = calendar.id
        )
        val taskDto = TaskDto(
            id = UUID.randomUUID(),
            name = "Task",
            description = "",
            status = TaskStatus.TODO,
            calendarId = calendar.id
        )
        val noteDto = NoteDto(
            id = UUID.randomUUID(),
            name = "Note",
            description = "",
            calendarId = calendar.id
        )
        whenever(_eventService.getAllByCategoryId(id)).thenReturn(listOf(eventDto))
        whenever(_taskService.getAllByCategoryId(id)).thenReturn(listOf(taskDto))
        whenever(_noteService.getAllByCategoryId(id)).thenReturn(listOf(noteDto))

        val response: ResponseEntity<List<Map<String, Any>>> =
            _controller.getAllItems(id, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(3, response.body?.size)
        val types = response.body?.mapNotNull { it["type"] as? String } ?: emptyList()
        assertTrue(types.containsAll(listOf("event", "task", "note")))

        verify(_eventService).getAllByCategoryId(id)
        verify(_taskService).getAllByCategoryId(id)
        verify(_noteService).getAllByCategoryId(id)
    }

    @Test
    fun `should update category with status code 200 OK`() {
        val dto = _sampleCategory.toDto()
        val updatedDto = dto.copy(name = "Work")
        whenever(_categoryService.update(id = _sampleCategory.id, dto)).thenReturn(updatedDto)

        val response: ResponseEntity<CategoryDto> =
            _controller.update(id = _sampleCategory.id, dto)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updatedDto, response.body)

        verify(_categoryService).update(id = _sampleCategory.id, dto)
    }

    @Test
    fun `should delete category with status code 204 No Content`() {
        doNothing().whenever(_categoryService).delete(id = _sampleCategory.id)

        val response: ResponseEntity<Void> =
            _controller.delete(id = _sampleCategory.id)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

        verify(_categoryService).delete(id = _sampleCategory.id)
    }

}
