package com.tomaszwnuk.opencalendar.task

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.calendar.Calendar
import com.tomaszwnuk.opencalendar.category.Category
import com.tomaszwnuk.opencalendar.category.CategoryColorHelper
import org.junit.jupiter.api.Assertions.assertEquals
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
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.awt.Color
import java.util.*

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TaskControllerTest {

    @Mock
    private lateinit var _taskService: TaskService

    @InjectMocks
    private lateinit var _taskController: TaskController

    private lateinit var _sampleTask: Task

    private lateinit var _sampleDto: TaskDto

    private lateinit var _pageable: Pageable

    @BeforeEach
    fun setup() {
        val sampleCalendar = Calendar(
            id = UUID.randomUUID(),
            title = "Personal",
            emoji = "\\uD83C\\uDFE0"
        )
        val sampleCategory = Category(
            id = UUID.randomUUID(),
            title = "Training",
            color = CategoryColorHelper.toHex(Color.GREEN)
        )
        _sampleTask = Task(
            id = UUID.randomUUID(),
            title = "Gym Workout",
            description = null,
            status = TaskStatus.TODO,
            calendar = sampleCalendar,
            category = sampleCategory
        )
        _sampleDto = _sampleTask.toDto()
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created task with status code 201 Created`() {
        whenever(_taskService.create(any())).thenReturn(_sampleTask)
        val response: ResponseEntity<TaskDto> = _taskController.create(_sampleDto)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(_sampleDto, response.body)
        verify(_taskService).create(_sampleDto)
    }

    @Test
    fun `should return paginated list of tasks with status code 200 OK`() {
        val tasks: List<Task> = listOf(_sampleTask, _sampleTask, _sampleTask)

        whenever(_taskService.getAll(_pageable)).thenReturn(PageImpl(tasks))
        val response: ResponseEntity<Page<TaskDto>> = _taskController.getAll(_pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(tasks.size, response.body?.totalElements?.toInt())
        verify(_taskService).getAll(_pageable)
    }

    @Test
    fun `should return task by id with status code 200 OK`() {
        val id: UUID = _sampleTask.id

        whenever(_taskService.getById(id)).thenReturn(_sampleTask)
        val response: ResponseEntity<TaskDto> = _taskController.getById(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(id, response.body?.id)
        verify(_taskService).getById(id)
    }

    @Test
    fun `should return filtered list of tasks with status code 200 OK`() {
        val filter = TaskFilterDto(title = "Gym Workout")
        val tasks: List<Task> = listOf(_sampleTask, _sampleTask, _sampleTask)

        whenever(_taskService.filter(eq(filter), eq(_pageable))).thenReturn(PageImpl(tasks))
        val response: ResponseEntity<Page<TaskDto>> = _taskController.filter(
            eq(filter.title),
            null,
            null,
            null,
            null,
            eq(_pageable)
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(tasks.size, response.body?.totalElements?.toInt())
        verify(_taskService).filter(eq(filter), eq(_pageable))
    }

    @Test
    fun `should return updated task with status code 200 OK`() {
        val updated: Task = _sampleTask.copy(title = "Updated Task")

        whenever(_taskService.update(_sampleTask.id, _sampleDto)).thenReturn(updated)
        val response: ResponseEntity<TaskDto> = _taskController.update(_sampleTask.id, _sampleDto)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updated.toDto(), response.body)
        verify(_taskService).update(_sampleTask.id, _sampleDto)
    }

    @Test
    fun `should delete task with status code 204 No Content`() {
        doNothing().whenever(_taskService).delete(_sampleTask.id)
        val response: ResponseEntity<Void> = _taskController.delete(_sampleTask.id)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        verify(_taskService).delete(_sampleTask.id)
    }

}
