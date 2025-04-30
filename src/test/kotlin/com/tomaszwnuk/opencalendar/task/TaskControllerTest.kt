/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.task

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.domain.task.*
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
import java.util.*

/**
 * Unit tests for the `TaskController` class.
 * Verifies the behavior of the controller's endpoints using mocked dependencies.
 */
@ExtendWith(MockitoExtension::class)
internal class TaskControllerTest {

    /**
     * Mocked instance of `TaskService` for simulating task-related operations.
     */
    @Mock
    private lateinit var _taskService: TaskService

    /**
     * Injected instance of `TaskController` with mocked dependencies.
     */
    @InjectMocks
    private lateinit var _controller: TaskController

    /**
     * Pageable instance for simulating pagination in tests.
     */
    private lateinit var _pageable: Pageable

    /**
     * Sample UUID used for testing.
     */
    private lateinit var _sampleId: UUID

    /**
     * Sample `TaskDto` instance used in tests.
     */
    private lateinit var _sampleDto: TaskDto

    /**
     * Sets up the test environment before each test.
     * Initializes `Pageable`, sample UUID, and sample `TaskDto`.
     */
    @BeforeEach
    fun setUp() {
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
        _sampleId = UUID.randomUUID()
        _sampleDto = TaskDto(
            id = _sampleId,
            title = "Code Review",
            description = "Review PRs and provide feedback",
            status = TaskStatus.TODO,
            calendarId = UUID.randomUUID(),
            categoryId = UUID.randomUUID()
        )
    }

    /**
     * Tests the creation of a task.
     * Verifies that the endpoint returns a 201 Created status and the created task.
     */
    @Test
    fun `should create task with status code 201 Created`() {
        whenever(_taskService.create(eq(_sampleDto))).thenReturn(_sampleDto)

        val response: ResponseEntity<TaskDto> = _controller.create(_sampleDto)

        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.body == _sampleDto)
        verify(_taskService).create(eq(_sampleDto))
    }

    /**
     * Tests retrieving all tasks.
     * Verifies that the endpoint returns a 200 OK status and a list of tasks.
     */
    @Test
    fun `should return all tasks with status code 200 OK`() {
        val taskOne =
            _sampleDto.copy(id = UUID.randomUUID(), title = "Release Planning", status = TaskStatus.IN_PROGRESS)
        val taskTwo = _sampleDto.copy(id = UUID.randomUUID(), title = "Sprint Retrospective", status = TaskStatus.DONE)
        whenever(_taskService.getAll()).thenReturn(listOf(taskOne, taskTwo))

        val response: ResponseEntity<Page<TaskDto>> =
            _controller.getAll(_pageable)

        assert(response.statusCode == HttpStatus.OK)
        assert(response.body?.totalElements == 2L)
        val titles = response.body?.content?.map { it.title } ?: emptyList()
        assert(titles.containsAll(listOf("Release Planning", "Sprint Retrospective")))
        verify(_taskService).getAll()
    }

    /**
     * Tests retrieving a task by its ID.
     * Verifies that the endpoint returns a 200 OK status and the requested task.
     */
    @Test
    fun `should return task by id with status code 200 OK`() {
        whenever(_taskService.getById(_sampleId)).thenReturn(_sampleDto)

        val response: ResponseEntity<TaskDto> = _controller.getById(_sampleId)

        assert(response.statusCode == HttpStatus.OK)
        assert(response.body == _sampleDto)
        verify(_taskService).getById(_sampleId)
    }

    /**
     * Tests filtering tasks based on criteria.
     * Verifies that the endpoint returns a 200 OK status and a list of filtered tasks.
     */
    @Test
    fun `should return filtered tasks with status code 200 OK`() {
        val filteredDto = _sampleDto.copy(id = UUID.randomUUID(), title = "Deployment")
        whenever(_taskService.filter(any<TaskFilterDto>())).thenReturn(listOf(filteredDto))

        val response: ResponseEntity<Page<TaskDto>> = _controller.filter(
            title = "Deploy",
            description = "release",
            status = TaskStatus.TODO.name,
            calendarId = _sampleDto.calendarId,
            categoryId = _sampleDto.categoryId,
            pageable = _pageable
        )

        assert(response.statusCode == HttpStatus.OK)
        assert(response.body?.totalElements == 1L)
        assert(response.body?.content?.first()?.title == "Deployment")
        verify(_taskService).filter(any<TaskFilterDto>())
    }

    /**
     * Tests updating a task.
     * Verifies that the endpoint returns a 200 OK status and the updated task.
     */
    @Test
    fun `should update task with status code 200 OK`() {
        val updatedDto = _sampleDto.copy(status = TaskStatus.IN_PROGRESS)
        whenever(_taskService.update(_sampleId, _sampleDto)).thenReturn(updatedDto)

        val response: ResponseEntity<TaskDto> = _controller.update(_sampleId, _sampleDto)

        assert(response.statusCode == HttpStatus.OK)
        assert(response.body == updatedDto)
        verify(_taskService).update(_sampleId, _sampleDto)
    }

    /**
     * Tests deleting a task.
     * Verifies that the endpoint returns a 204 No Content status.
     */
    @Test
    fun `should delete task with status code 204 No Content`() {
        doNothing().whenever(_taskService).delete(_sampleId)

        val response: ResponseEntity<Void> = _controller.delete(_sampleId)

        assert(response.statusCode == HttpStatus.NO_CONTENT)
        verify(_taskService).delete(_sampleId)
    }

}
