//package com.tomaszwnuk.opencalendar.task
//
//import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
//import com.tomaszwnuk.opencalendar.domain.calendar.CalendarRepository
//import com.tomaszwnuk.opencalendar.domain.category.Category
//import com.tomaszwnuk.opencalendar.domain.category.CategoryRepository
//import com.tomaszwnuk.opencalendar.domain.task.*
//import com.tomaszwnuk.opencalendar.domain.user.UserService
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.assertThrows
//import org.junit.jupiter.api.extension.ExtendWith
//import org.mockito.Mock
//import org.mockito.junit.jupiter.MockitoExtension
//import org.mockito.kotlin.*
//import java.util.*
//import kotlin.test.assertEquals
//
//@ExtendWith(MockitoExtension::class)
//internal class TaskServiceTest {
//
//    @Mock
//    private lateinit var _taskRepository: TaskRepository
//    @Mock
//    private lateinit var _calendarRepository: CalendarRepository
//    @Mock
//    private lateinit var _categoryRepository: CategoryRepository
//    @Mock
//    private lateinit var _userService: UserService
//
//    private lateinit var _service: TaskService
//    private lateinit var _userId: UUID
//    private lateinit var _calendar: Calendar
//    private lateinit var _category: Category
//    private lateinit var _task: Task
//    private lateinit var _taskList: List<Task>
//
//    @BeforeEach
//    fun setUp() {
//        _service = TaskService(_taskRepository, _calendarRepository, _categoryRepository, _userService)
//        _userId = UUID.randomUUID()
//
//        _calendar = Calendar(UUID.randomUUID(), "Development Calendar", "💻", _userId)
//        _category = Category(UUID.randomUUID(), "High Priority", "#FF4500", _userId)
//        _task = Task(UUID.randomUUID(), "Refactor", "Improve architecture", TaskStatus.TODO, _calendar, _category)
//        _taskList = listOf(_task, _task.copy(), _task.copy())
//    }
//
//    @Test
//    fun `should return created task`() {
//        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
//        whenever(_calendarRepository.findByIdAndUserId(_calendar.id, _userId))
//            .thenReturn(Optional.of(_calendar))
//        whenever(_categoryRepository.findByIdAndUserId(_category.id, _userId))
//            .thenReturn(Optional.of(_category))
//        whenever(_taskRepository.save(any())).thenReturn(_task)
//
//        val result = _service.create(_task.toDto())
//
//        assertEquals(_task.toDto(), result)
//        verify(_taskRepository).save(any())
//    }
//
//    @Test
//    fun `should throw error when creating task with missing calendar`() {
//        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
//        whenever(_calendarRepository.findByIdAndUserId(_calendar.id, _userId))
//            .thenReturn(Optional.empty())
//
//        assertThrows<IllegalArgumentException> { _service.create(_task.toDto()) }
//        verify(_taskRepository, never()).save(any())
//    }
//
//    @Test
//    fun `should return task by id`() {
//        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
//        whenever(_taskRepository.findByIdAndCalendarUserId(_task.id, _userId))
//            .thenReturn(Optional.of(_task))
//
//        val result = _service.getById(_task.id)
//
//        assertEquals(_task.toDto(), result)
//        verify(_taskRepository).findByIdAndCalendarUserId(_task.id, _userId)
//    }
//
//    @Test
//    fun `should throw error when task id not found`() {
//        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
//        whenever(_taskRepository.findByIdAndCalendarUserId(_task.id, _userId))
//            .thenReturn(Optional.empty())
//
//        assertThrows<NoSuchElementException> { _service.getById(_task.id) }
//        verify(_taskRepository).findByIdAndCalendarUserId(_task.id, _userId)
//    }
//
//    @Test
//    fun `should return all tasks`() {
//        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
//        whenever(_taskRepository.findAllByCalendarUserId(_userId)).thenReturn(_taskList)
//
//        val result = _service.getAll()
//
//        assertEquals(_taskList.map { it.toDto() }, result)
//        verify(_taskRepository).findAllByCalendarUserId(_userId)
//    }
//
//    @Test
//    fun `should return tasks by calendar id`() {
//        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
//        whenever(_taskRepository.findAllByCalendarIdAndCalendarUserId(_calendar.id, _userId))
//            .thenReturn(_taskList)
//
//        val result = _service.getAllByCalendarId(_calendar.id)
//
//        assertEquals(_taskList.map { it.toDto() }, result)
//        verify(_taskRepository).findAllByCalendarIdAndCalendarUserId(_calendar.id, _userId)
//    }
//
//    @Test
//    fun `should return tasks by category id`() {
//        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
//        whenever(_taskRepository.findAllByCategoryIdAndCalendarUserId(_category.id, _userId))
//            .thenReturn(_taskList)
//
//        val result = _service.getAllByCategoryId(_category.id)
//
//        assertEquals(_taskList.map { it.toDto() }, result)
//        verify(_taskRepository).findAllByCategoryIdAndCalendarUserId(_category.id, _userId)
//    }
//
//    @Test
//    fun `should return filtered tasks`() {
//        val filter = TaskFilterDto(
//            name = "Refactor",
//            description = null,
//            calendarId = null,
//            categoryId = null,
//            status = null
//        )
//
//        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
//        whenever(
//            _taskRepository.filter(
//                userId = _userId,
//                name = filter.name,
//                description = filter.description,
//                calendarId = filter.calendarId,
//                categoryId = filter.categoryId,
//                status = filter.status
//            )
//        ).thenReturn(_taskList)
//
//        val result = _service.filter(filter)
//
//        assertEquals(_taskList.map { it.toDto() }, result)
//        verify(_taskRepository).filter(
//            userId = _userId,
//            name = filter.name,
//            description = filter.description,
//            calendarId = filter.calendarId,
//            categoryId = filter.categoryId,
//            status = filter.status
//        )
//    }
//
//    @Test
//    fun `should return updated task`() {
//        val dto = _task.toDto().copy(name = "Updated")
//
//        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
//        whenever(_taskRepository.findByIdAndCalendarUserId(_task.id, _userId))
//            .thenReturn(Optional.of(_task))
//        whenever(_calendarRepository.findByIdAndUserId(dto.calendarId, _userId))
//            .thenReturn(Optional.of(_calendar))
//        whenever(_categoryRepository.findByIdAndUserId(dto.categoryId!!, _userId))
//            .thenReturn(Optional.of(_category))
//        whenever(_taskRepository.save(_task)).thenAnswer { it.arguments[0] as Task }
//
//        val result = _service.update(_task.id, dto)
//
//        assertEquals(dto, result)
//        verify(_taskRepository).save(_task)
//    }
//
//    @Test
//    fun `should throw error when updating non existing task`() {
//        val dto = _task.toDto()
//
//        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
//        whenever(_taskRepository.findByIdAndCalendarUserId(_task.id, _userId))
//            .thenReturn(Optional.empty())
//
//        assertThrows<IllegalArgumentException> { _service.update(_task.id, dto) }
//        verify(_taskRepository, never()).save(any())
//    }
//
//    @Test
//    fun `should delete task`() {
//        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
//        whenever(_taskRepository.findByIdAndCalendarUserId(_task.id, _userId))
//            .thenReturn(Optional.of(_task))
//        doNothing().whenever(_taskRepository).delete(_task)
//
//        _service.delete(_task.id)
//
//        verify(_taskRepository).findByIdAndCalendarUserId(_task.id, _userId)
//        verify(_taskRepository).delete(_task)
//    }
//
//    @Test
//    fun `should delete all tasks by calendar id`() {
//        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
//        whenever(_taskRepository.findAllByCalendarIdAndCalendarUserId(_calendar.id, _userId))
//            .thenReturn(_taskList)
//        doNothing().whenever(_taskRepository).deleteAll(_taskList)
//
//        _service.deleteAllByCalendarId(_calendar.id)
//
//        verify(_taskRepository).findAllByCalendarIdAndCalendarUserId(_calendar.id, _userId)
//        verify(_taskRepository).deleteAll(_taskList)
//    }
//
//    @Test
//    fun `should clear category for all tasks by category id`() {
//        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
//        whenever(_taskRepository.findAllByCategoryIdAndCalendarUserId(_category.id, _userId))
//            .thenReturn(_taskList)
//        whenever(_taskRepository.save(any())).thenAnswer { it.arguments[0] as Task }
//
//        _service.removeCategoryByCategoryId(_category.id)
//
//        verify(_taskRepository).findAllByCategoryIdAndCalendarUserId(_category.id, _userId)
//        verify(_taskRepository, times(_taskList.size)).save(argThat { this.category == null })
//    }
//}
