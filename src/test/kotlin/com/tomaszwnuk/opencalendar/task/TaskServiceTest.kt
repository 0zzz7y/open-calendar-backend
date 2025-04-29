package com.tomaszwnuk.opencalendar.task

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.calendar.Calendar
import com.tomaszwnuk.opencalendar.calendar.CalendarRepository
import com.tomaszwnuk.opencalendar.category.Category
import com.tomaszwnuk.opencalendar.category.CategoryColorHelper
import com.tomaszwnuk.opencalendar.category.CategoryRepository
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
import java.util.*

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TaskServiceTest {

    @Mock
    private lateinit var taskRepository: TaskRepository

    @Mock
    private lateinit var calendarRepository: CalendarRepository

    @Mock
    private lateinit var categoryRepository: CategoryRepository

    @InjectMocks
    private lateinit var taskService: TaskService

    private lateinit var sampleCalendar: Calendar

    private lateinit var sampleCategory: Category

    private lateinit var sampleTask: Task

    private lateinit var sampleTaskDto: TaskDto

    private lateinit var pageable: Pageable

    @BeforeEach
    fun setup() {
        sampleCalendar = Calendar(
            id = UUID.randomUUID(),
            title = "Personal",
            emoji = "\uD83C\uDFE0"
        )
        sampleCategory = Category(
            id = UUID.randomUUID(),
            title = "Training",
            color = CategoryColorHelper.toHex(Color.GREEN)
        )
        sampleTask = Task(
            id = UUID.randomUUID(),
            title = "Daily Standup",
            description = null,
            status = TaskStatus.TODO,
            calendar = sampleCalendar,
            category = sampleCategory
        )
        sampleTaskDto = sampleTask.toDto()
        pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created task`() {
        whenever(calendarRepository.findById(sampleTaskDto.calendarId)).thenReturn(Optional.of(sampleCalendar))
        whenever(categoryRepository.findById(sampleTaskDto.categoryId!!)).thenReturn(Optional.of(sampleCategory))
        doReturn(sampleTask).whenever(taskRepository).save(any())

        val result: Task = taskService.create(sampleTaskDto)

        assertNotNull(result)
        assertEquals(sampleTask.id, result.id)
        assertEquals(sampleTask.title, result.title)
        assertEquals(sampleTask.status, result.status)

        verify(taskRepository).save(any())
    }

    @Test
    fun `should return paginated list of all tasks`() {
        val tasks: List<Task> = listOf(sampleTask, sampleTask.copy(), sampleTask.copy())
        whenever(taskRepository.findAll(pageable)).thenReturn(PageImpl(tasks))

        val result: Page<Task> = taskService.getAll(pageable)

        assertEquals(tasks.size, result.totalElements.toInt())
        assertEquals(tasks.map { it.id }, result.content.map { it.id })
        assertEquals(tasks.map { it.title }, result.content.map { it.title })

        verify(taskRepository).findAll(pageable)
    }

    @Test
    fun `should return task by id`() {
        val id: UUID = sampleTask.id
        whenever(taskRepository.findById(id)).thenReturn(Optional.of(sampleTask))

        val result: Task = taskService.getById(id)

        assertNotNull(result)
        assertEquals(sampleTask.id, result.id)
        assertEquals(sampleTask.title, result.title)
        assertEquals(sampleTask.status, result.status)

        verify(taskRepository).findById(id)
    }

    @Test
    fun `should return paginated list of filtered tasks`() {
        val filter = TaskFilterDto(title = "Task")
        val tasks: List<Task> = listOf(sampleTask, sampleTask.copy(), sampleTask.copy())

        whenever(
            taskRepository.filter(
                eq(filter.title),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(pageable)
            )
        ).thenReturn(PageImpl(tasks))

        val result: Page<Task> = taskService.filter(filter, pageable)

        assertEquals(tasks.size, result.totalElements.toInt())
        assertEquals(tasks.map { it.title }, result.content.map { it.title })

        verify(taskRepository).filter(
            eq(filter.title),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            eq(pageable)
        )
    }

    @Test
    fun `should return updated task`() {
        val updatedTask: Task = sampleTask.copy(title = "Updated Task")

        whenever(taskRepository.findById(sampleTask.id)).thenReturn(Optional.of(sampleTask))
        whenever(calendarRepository.findById(sampleTaskDto.calendarId)).thenReturn(Optional.of(sampleCalendar))
        whenever(categoryRepository.findById(sampleTaskDto.categoryId!!)).thenReturn(Optional.of(sampleCategory))
        doReturn(updatedTask).whenever(taskRepository).save(any())

        val result: Task = taskService.update(sampleTask.id, sampleTaskDto)

        assertNotNull(result)
        assertEquals(updatedTask.id, result.id)
        assertEquals("Updated Task", result.title)

        verify(taskRepository).save(any())
    }

    @Test
    fun `should delete task by id`() {
        val id: UUID = sampleTask.id
        whenever(taskRepository.findById(id)).thenReturn(Optional.of(sampleTask))
        doNothing().whenever(taskRepository).delete(sampleTask)

        taskService.delete(id)

        verify(taskRepository).delete(sampleTask)
    }

}
