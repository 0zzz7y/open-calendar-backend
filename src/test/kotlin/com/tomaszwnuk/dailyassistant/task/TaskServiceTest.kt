package com.tomaszwnuk.dailyassistant.task

import com.tomaszwnuk.dailyassistant.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.dailyassistant.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.dailyassistant.calendar.Calendar
import com.tomaszwnuk.dailyassistant.calendar.CalendarRepository
import com.tomaszwnuk.dailyassistant.category.Category
import com.tomaszwnuk.dailyassistant.category.CategoryRepository
import com.tomaszwnuk.dailyassistant.domain.RecurringPattern
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
import java.time.LocalDateTime
import java.util.*

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

    private lateinit var _pageable: Pageable

    private lateinit var _sampleTask: Task

    private lateinit var _sampleDto: TaskDto

    @BeforeEach
    fun setup() {
        _sampleCalendar = Calendar(name = "Personal")
        _sampleCategory = Category(name = "Training")
        _sampleTask = Task(
            id = UUID.randomUUID(),
            name = "Gym Workout",
            description = null,
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().plusHours(1),
            recurringPattern = RecurringPattern.WEEKLY,
            status = TaskStatus.TODO,
            calendar = _sampleCalendar,
            category = _sampleCategory
        )
        _sampleDto = _sampleTask.toDto()
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created task`() {
        whenever(_calendarRepository.findById(_sampleDto.calendarId!!)).thenReturn(Optional.of(_sampleCalendar))
        whenever(_categoryRepository.findById(_sampleDto.categoryId!!)).thenReturn(Optional.of(_sampleCategory))
        doReturn(_sampleTask).whenever(_taskRepository).save(any())
        val result: Task = _taskService.create(_sampleDto)

        assertEquals(_sampleTask.id, result.id)
        verify(_taskRepository).save(any())
    }

    @Test
    fun `should return paged list of tasks`() {
        val tasks: List<Task> = listOf(_sampleTask, _sampleTask, _sampleTask)

        whenever(_taskRepository.findAll(_pageable)).thenReturn(PageImpl(tasks))
        val result: Page<Task> = _taskService.getAll(_pageable)

        assertEquals(tasks.size, result.totalElements.toInt())
        verify(_taskRepository).findAll(_pageable)
    }

    @Test
    fun `should return task by id`() {
        val id: UUID = _sampleTask.id

        whenever(_taskRepository.findById(id)).thenReturn(Optional.of(_sampleTask))
        val result: Task = _taskService.getById(id)

        assertEquals(_sampleTask.name, result.name)
        verify(_taskRepository).findById(id)
    }

    @Test
    fun `should return filtered tasks`() {
        val filter = TaskFilterDto(name = "Task")
        val tasks: List<Task> = listOf(_sampleTask, _sampleTask, _sampleTask)

        whenever(
            _taskRepository.filter(
                eq(filter.name),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(_pageable)
            )
        ).thenReturn(PageImpl(tasks))
        val result: Page<Task> = _taskService.filter(filter, _pageable)

        assertEquals(tasks.size, result.totalElements.toInt())
        verify(_taskRepository).filter(
            eq(filter.name),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            any()
        )
    }

    @Test
    fun `should return updated task`() {
        val id: UUID = _sampleTask.id
        val updated: Task = _sampleTask.copy(name = "Updated Task")

        whenever(_taskRepository.findById(id)).thenReturn(Optional.of(_sampleTask))
        whenever(_calendarRepository.findById(_sampleDto.calendarId!!)).thenReturn(Optional.of(_sampleCalendar))
        whenever(_categoryRepository.findById(_sampleDto.categoryId!!)).thenReturn(Optional.of(_sampleCategory))
        doReturn(updated).whenever(_taskRepository).save(any())
        val result: Task = _taskService.update(id, _sampleDto)

        assertEquals(updated.name, result.name)
        verify(_taskRepository).save(any())
    }

    @Test
    fun `should delete task`() {
        val id: UUID = _sampleTask.id

        whenever(_taskRepository.findById(id)).thenReturn(Optional.of(_sampleTask))
        doNothing().whenever(_taskRepository).delete(_sampleTask)
        _taskService.delete(id)

        verify(_taskRepository).delete(_sampleTask)
    }

}
