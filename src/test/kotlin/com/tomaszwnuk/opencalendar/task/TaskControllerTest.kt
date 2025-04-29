package com.tomaszwnuk.opencalendar.task

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.calendar.Calendar
import com.tomaszwnuk.opencalendar.category.Category
import com.tomaszwnuk.opencalendar.category.CategoryColorHelper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
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
    private lateinit var taskService: TaskService

    @InjectMocks
    private lateinit var taskController: TaskController

    private lateinit var sampleTask: Task

    private lateinit var sampleTaskDto: TaskDto

    private lateinit var pageable: Pageable

    @BeforeEach
    fun setup() {
        val sampleCalendar = Calendar(
            id = UUID.randomUUID(),
            title = "Personal",
            emoji = "\uD83C\uDFE0"
        )
        val sampleCategory = Category(
            id = UUID.randomUUID(),
            title = "Training",
            color = CategoryColorHelper.toHex(Color.GREEN)
        )
        sampleTask = Task(
            id = UUID.randomUUID(),
            title = "Gym Workout",
            description = null,
            status = TaskStatus.TODO,
            calendar = sampleCalendar,
            category = sampleCategory
        )
        sampleTaskDto = sampleTask.toDto()
        pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created task with status code 201 Created`() {
        whenever(taskService.create(sampleTaskDto)).thenReturn(sampleTask)

        val response: ResponseEntity<TaskDto> = taskController.create(sampleTaskDto)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        assertEquals(sampleTask.id, response.body?.id)
        assertEquals(sampleTask.title, response.body?.title)
        assertEquals(sampleTask.status, response.body?.status)

        verify(taskService).create(sampleTaskDto)
    }

    @Test
    fun `should return paginated list of all tasks with status code 200 OK`() {
        val tasks: List<Task> = listOf(sampleTask, sampleTask.copy(), sampleTask.copy())
        whenever(taskService.getAll(pageable)).thenReturn(PageImpl(tasks))

        val response: ResponseEntity<Page<TaskDto>> = taskController.getAll(pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(tasks.size.toLong(), response.body?.totalElements)
        assertEquals(tasks.map { it.id }, response.body?.content?.map { it.id })
        assertEquals(tasks.map { it.title }, response.body?.content?.map { it.title })

        verify(taskService).getAll(pageable)
    }

    @Test
    fun `should return task by id with status code 200 OK`() {
        val id: UUID = sampleTask.id
        whenever(taskService.getById(id)).thenReturn(sampleTask)

        val response: ResponseEntity<TaskDto> = taskController.getById(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(sampleTask.id, response.body?.id)
        assertEquals(sampleTask.title, response.body?.title)
        assertEquals(sampleTask.status, response.body?.status)

        verify(taskService).getById(id)
    }

    @Test
    fun `should return paginated list of filtered tasks with status code 200 OK`() {
        val filter = TaskFilterDto(title = "Gym Workout")
        val tasks: List<Task> = listOf(sampleTask, sampleTask.copy(), sampleTask.copy())

        whenever(taskService.filter(eq(filter), eq(pageable))).thenReturn(PageImpl(tasks))

        val response: ResponseEntity<Page<TaskDto>> = taskController.filter(
            filter.title,
            null,
            null,
            null,
            null,
            pageable
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(tasks.size.toLong(), response.body?.totalElements)
        assertEquals(tasks.map { it.title }, response.body?.content?.map { it.title })

        verify(taskService).filter(eq(filter), eq(pageable))
    }

    @Test
    fun `should return updated task with status code 200 OK`() {
        val updatedTask: Task = sampleTask.copy(title = "Updated Task")
        whenever(taskService.update(sampleTask.id, sampleTaskDto)).thenReturn(updatedTask)

        val response: ResponseEntity<TaskDto> = taskController.update(sampleTask.id, sampleTaskDto)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(updatedTask.id, response.body?.id)
        assertEquals("Updated Task", response.body?.title)

        verify(taskService).update(sampleTask.id, sampleTaskDto)
    }

    @Test
    fun `should delete task with status code 204 No Content`() {
        doNothing().whenever(taskService).delete(sampleTask.id)

        val response: ResponseEntity<Void> = taskController.delete(sampleTask.id)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

        verify(taskService).delete(sampleTask.id)
    }

}
