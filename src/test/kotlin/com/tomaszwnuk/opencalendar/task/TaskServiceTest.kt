package com.tomaszwnuk.opencalendar.task

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.calendar.CalendarRepository
import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.category.CategoryColorHelper
import com.tomaszwnuk.opencalendar.domain.category.CategoryRepository
import com.tomaszwnuk.opencalendar.domain.task.Task
import com.tomaszwnuk.opencalendar.domain.task.TaskDto
import com.tomaszwnuk.opencalendar.domain.task.TaskFilterDto
import com.tomaszwnuk.opencalendar.domain.task.TaskRepository
import com.tomaszwnuk.opencalendar.domain.task.TaskService
import com.tomaszwnuk.opencalendar.domain.task.TaskStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
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
import java.awt.Color
import java.util.Optional
import java.util.UUID

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TaskServiceTest {

    @Mock
    private lateinit var _taskRepository: TaskRepository

    @Mock
    private lateinit var _calendarRepository: CalendarRepository

    @Mock
    private lateinit var _categoryRepository: CategoryRepository

    @InjectMocks
    private lateinit var _taskService: TaskService

    private lateinit var _sampleCalendar: Calendar

    private lateinit var _sampleCategory: Category

    private lateinit var _sampleTask: Task

    private lateinit var _sampleTaskDto: TaskDto

    private lateinit var _pageable: Pageable

    @BeforeEach
    fun setup() {
        _sampleCalendar = Calendar(
            id = UUID.randomUUID(),
            title = "Personal",
            emoji = "\uD83C\uDFE0"
        )
        _sampleCategory = Category(
            id = UUID.randomUUID(),
            title = "Training",
            color = CategoryColorHelper.toHex(Color.GREEN)
        )
        _sampleTask = Task(
            id = UUID.randomUUID(),
            title = "Daily Standup",
            description = null,
            status = TaskStatus.TODO,
            calendar = _sampleCalendar,
            category = _sampleCategory
        )
        _sampleTaskDto = _sampleTask.toDto()
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created task`() {
        whenever(_calendarRepository.findById(_sampleTaskDto.calendarId)).thenReturn(Optional.of(_sampleCalendar))
        whenever(_categoryRepository.findById(_sampleTaskDto.categoryId!!)).thenReturn(Optional.of(_sampleCategory))
        doReturn(_sampleTask).whenever(_taskRepository).save(any())

        val result: Task = _taskService.create(_sampleTaskDto)

        assertNotNull(result)
        assertEquals(_sampleTask.id, result.id)
        assertEquals(_sampleTask.title, result.title)
        assertEquals(_sampleTask.status, result.status)

        verify(_taskRepository).save(any())
    }

    @Test
    fun `should return paginated list of all tasks`() {
        val tasks: List<Task> = listOf(_sampleTask, _sampleTask.copy(), _sampleTask.copy())
        whenever(_taskRepository.findAll(_pageable)).thenReturn(PageImpl(tasks))

        val result: Page<Task> = _taskService.getAll(_pageable)

        assertEquals(tasks.size, result.totalElements.toInt())
        assertEquals(tasks.map { it.id }, result.content.map { it.id })
        assertEquals(tasks.map { it.title }, result.content.map { it.title })

        verify(_taskRepository).findAll(_pageable)
    }

    @Test
    fun `should return task by id`() {
        val id: UUID = _sampleTask.id
        whenever(_taskRepository.findById(id)).thenReturn(Optional.of(_sampleTask))

        val result: Task = _taskService.getById(id)

        assertNotNull(result)
        assertEquals(_sampleTask.id, result.id)
        assertEquals(_sampleTask.title, result.title)
        assertEquals(_sampleTask.status, result.status)

        verify(_taskRepository).findById(id)
    }

    @Test
    fun `should return paginated list of filtered tasks`() {
        val filter = TaskFilterDto(title = "Task")
        val tasks: List<Task> = listOf(_sampleTask, _sampleTask.copy(), _sampleTask.copy())

        whenever(
            _taskRepository.filter(
                eq(filter.title),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(_pageable)
            )
        ).thenReturn(PageImpl(tasks))

        val result: Page<Task> = _taskService.filter(filter, _pageable)

        assertEquals(tasks.size, result.totalElements.toInt())
        assertEquals(tasks.map { it.title }, result.content.map { it.title })

        verify(_taskRepository).filter(
            eq(filter.title),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            eq(_pageable)
        )
    }

    @Test
    fun `should return updated task`() {
        val updatedTask: Task = _sampleTask.copy(title = "Updated Task")

        whenever(_taskRepository.findById(_sampleTask.id)).thenReturn(Optional.of(_sampleTask))
        whenever(_calendarRepository.findById(_sampleTaskDto.calendarId)).thenReturn(Optional.of(_sampleCalendar))
        whenever(_categoryRepository.findById(_sampleTaskDto.categoryId!!)).thenReturn(Optional.of(_sampleCategory))
        doReturn(updatedTask).whenever(_taskRepository).save(any())

        val result: Task = _taskService.update(_sampleTask.id, _sampleTaskDto)

        assertNotNull(result)
        assertEquals(updatedTask.id, result.id)
        assertEquals("Updated Task", result.title)

        verify(_taskRepository).save(any())
    }

    @Test
    fun `should delete task by id`() {
        val id: UUID = _sampleTask.id
        whenever(_taskRepository.findById(id)).thenReturn(Optional.of(_sampleTask))
        doNothing().whenever(_taskRepository).delete(_sampleTask)

        _taskService.delete(id)

        verify(_taskRepository).delete(_sampleTask)
    }

}
