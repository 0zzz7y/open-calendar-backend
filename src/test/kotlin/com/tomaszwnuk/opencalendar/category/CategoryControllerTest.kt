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
import com.tomaszwnuk.opencalendar.domain.note.NoteDto
import com.tomaszwnuk.opencalendar.domain.note.NoteService
import com.tomaszwnuk.opencalendar.domain.other.RecurringPattern
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

/**
 * Unit tests for the `CategoryController` class.
 * Verifies the behavior of the controller's endpoints using mocked dependencies.
 */
@ExtendWith(MockitoExtension::class)
internal class CategoryControllerTest {

    /**
     * Mocked instance of `CategoryService` for simulating category-related operations.
     */
    @Mock
    private lateinit var _categoryService: CategoryService

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
     * Injected instance of `CategoryController` with mocked dependencies.
     */
    @InjectMocks
    private lateinit var _controller: CategoryController

    /**
     * MockMvc instance for simulating HTTP requests to the controller.
     */
    private lateinit var _mockMvc: MockMvc

    /**
     * ObjectMapper instance for serializing and deserializing JSON.
     */
    private lateinit var _objectMapper: ObjectMapper

    /**
     * Sample `Category` instance used in tests.
     */
    private lateinit var _sampleCategory: Category

    /**
     * Sample `CategoryDto` instance used in tests.
     */
    private lateinit var _sampleCategoryDto: CategoryDto

    /**
     * Pageable instance for simulating pagination in tests.
     */
    private lateinit var _pageable: Pageable

    /**
     * Sets up the test environment before each test.
     * Initializes `MockMvc`, `ObjectMapper`, and sample data.
     */
    @BeforeEach
    fun setUp() {
        _sampleCategory = Category(
            id = UUID.randomUUID(),
            title = "Personal",
            color = CategoryColorHelper.toHex(Color.GREEN)
        )
        _sampleCategoryDto = _sampleCategory.toDto()
        _objectMapper = ObjectMapper()
        _pageable = org.springframework.data.domain.PageRequest.of(
            PAGEABLE_PAGE_NUMBER,
            PAGEABLE_PAGE_SIZE
        )
        _mockMvc = MockMvcBuilders
            .standaloneSetup(_controller)
            .setCustomArgumentResolvers(PageableHandlerMethodArgumentResolver())
            .build()
    }

    /**
     * Tests the creation of a category.
     * Verifies that the endpoint returns a 201 Created status and the created category.
     */
    @Test
    fun `should return created category with status code 201 Created`() {
        whenever(_categoryService.create(eq(_sampleCategoryDto))).thenReturn(_sampleCategoryDto)

        val response: ResponseEntity<CategoryDto> = _controller.create(_sampleCategoryDto)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(_sampleCategoryDto, response.body)

        verify(_categoryService).create(eq(_sampleCategoryDto))
    }

    /**
     * Tests retrieving all categories with pagination.
     * Verifies that the endpoint returns a 200 OK status and a paginated list of categories.
     */
    @Test
    fun `should return paginated list of all categories with status code 200 OK`() {
        val categories = listOf(_sampleCategory, _sampleCategory.copy(), _sampleCategory.copy())
        whenever(_categoryService.getAll()).thenReturn(categories.map { it.toDto() })

        val response: ResponseEntity<org.springframework.data.domain.Page<CategoryDto>> =
            _controller.getAll(_pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(categories.size.toLong(), response.body?.totalElements)
        assertEquals(categories.map { it.id }, response.body?.content?.map { it.id })

        verify(_categoryService).getAll()
    }

    /**
     * Tests retrieving a category by its ID.
     * Verifies that the endpoint returns a 200 OK status and the requested category.
     */
    @Test
    fun `should return category by id with status code 200 OK`() {
        val id = _sampleCategory.id
        whenever(_categoryService.getById(id)).thenReturn(_sampleCategoryDto)

        val response: ResponseEntity<CategoryDto> = _controller.getById(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(_sampleCategoryDto, response.body)

        verify(_categoryService).getById(id)
    }

    /**
     * Tests retrieving events associated with a category.
     * Verifies that the endpoint returns a 200 OK status and a paginated list of events.
     */
    @Test
    fun `should return paginated list of category events with status code 200 OK`() {
        val id = _sampleCategory.id
        val now = LocalDateTime.now()
        val calendar = Calendar(title = "Calendar", emoji = "📅")
        val eventDto = EventDto(
            id = UUID.randomUUID(),
            title = "Event",
            description = "Desc",
            startDate = now,
            endDate = now.plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendarId = calendar.id
        )
        whenever(_eventService.getAllByCategoryId(id)).thenReturn(listOf(eventDto))

        val response: ResponseEntity<org.springframework.data.domain.Page<EventDto>> =
            _controller.getEvents(id, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, response.body?.totalElements)
        assertEquals(eventDto.title, response.body?.content?.first()?.title)

        verify(_eventService).getAllByCategoryId(id)
    }

    /**
     * Tests retrieving tasks associated with a category.
     * Verifies that the endpoint returns a 200 OK status and a paginated list of tasks.
     */
    @Test
    fun `should return paginated list of category tasks with status code 200 OK`() {
        val id = _sampleCategory.id
        val calendar = Calendar(id = UUID.randomUUID(), title = "Calendar", emoji = "📅")
        val taskDto = TaskDto(
            id = UUID.randomUUID(),
            title = "Task",
            description = "Desc",
            status = TaskStatus.TODO,
            calendarId = calendar.id
        )
        whenever(_taskService.getAllByCategoryId(id)).thenReturn(listOf(taskDto))

        val response: ResponseEntity<org.springframework.data.domain.Page<TaskDto>> =
            _controller.getTasks(id, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, response.body?.totalElements)
        assertEquals(taskDto.title, response.body?.content?.first()?.title)

        verify(_taskService).getAllByCategoryId(id)
    }

    /**
     * Tests retrieving notes associated with a category.
     * Verifies that the endpoint returns a 200 OK status and a paginated list of notes.
     */
    @Test
    fun `should return paginated list of category notes with status code 200 OK`() {
        val id = _sampleCategory.id
        val calendar = Calendar(id = UUID.randomUUID(), title = "Calendar", emoji = "📅")
        val noteDto = NoteDto(
            id = UUID.randomUUID(),
            title = "Note",
            description = "Desc",
            calendarId = calendar.id
        )
        whenever(_noteService.getAllByCategoryId(id)).thenReturn(listOf(noteDto))

        val response: ResponseEntity<org.springframework.data.domain.Page<NoteDto>> =
            _controller.getNotes(id, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, response.body?.totalElements)
        assertEquals(noteDto.title, response.body?.content?.first()?.title)

        verify(_noteService).getAllByCategoryId(id)
    }

    /**
     * Tests retrieving a combined list of items (events, tasks, notes) associated with a category.
     * Verifies that the endpoint returns a 200 OK status and a combined list of items.
     */
    @Test
    fun `should return combined list of items with status code 200 OK`() {
        val id = _sampleCategory.id
        val now = LocalDateTime.now()
        val calendar = Calendar(title = "Calendar", emoji = "📅")
        val eventDto = EventDto(
            id = UUID.randomUUID(),
            title = "Event",
            description = "",
            startDate = now,
            endDate = now,
            recurringPattern = RecurringPattern.NONE,
            calendarId = calendar.id
        )
        val taskDto = TaskDto(
            id = UUID.randomUUID(),
            title = "Task",
            description = "",
            status = TaskStatus.TODO,
            calendarId = calendar.id
        )
        val noteDto = NoteDto(
            id = UUID.randomUUID(),
            title = "Note",
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

    /**
     * Tests updating a category.
     * Verifies that the endpoint returns a 200 OK status and the updated category.
     */
    @Test
    fun `should update category with status code 200 OK`() {
        val dto = _sampleCategory.toDto()
        val updatedDto = dto.copy(title = "Work")
        whenever(_categoryService.update(_sampleCategory.id, dto)).thenReturn(updatedDto)

        val response: ResponseEntity<CategoryDto> =
            _controller.update(_sampleCategory.id, dto)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updatedDto, response.body)

        verify(_categoryService).update(_sampleCategory.id, dto)
    }

    /**
     * Tests deleting a category.
     * Verifies that the endpoint returns a 204 No Content status.
     */
    @Test
    fun `should delete category with status code 204 No Content`() {
        doNothing().whenever(_categoryService).delete(_sampleCategory.id)

        val response: ResponseEntity<Void> =
            _controller.delete(_sampleCategory.id)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

        verify(_categoryService).delete(_sampleCategory.id)
    }

}
