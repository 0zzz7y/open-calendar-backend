package com.tomaszwnuk.opencalendar.domain.task

import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.calendar.CalendarRepository
import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.category.CategoryRepository
import com.tomaszwnuk.opencalendar.domain.user.UserService
import com.tomaszwnuk.opencalendar.utility.logger.info
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import java.util.*

@Service
class TaskService(
    private val _taskRepository: TaskRepository,
    private val _calendarRepository: CalendarRepository,
    private val _categoryRepository: CategoryRepository,
    private val _userService: UserService
) {

    private var _timer: Long = 0

    @Caching(
        evict = [
            CacheEvict(cacheNames = ["allTasks"], allEntries = true),
            CacheEvict(cacheNames = ["calendarTasks"], allEntries = true),
            CacheEvict(cacheNames = ["categoryTasks"], allEntries = true)
        ]
    )
    fun create(dto: TaskDto): TaskDto {
        info(this, "Creating $dto")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val calendar: Calendar = _calendarRepository.findByIdAndUserId(
            id = dto.calendarId,
            userId = userId
        ).orElseThrow { NoSuchElementException("Calendar with id ${dto.calendarId} not found for user $userId") }
        val category: Category? = dto.categoryId?.let {
            _categoryRepository.findByIdAndUserId(id = it, userId = userId).orElseThrow {
                NoSuchElementException("Category with id $it not found for user $userId")
            }
        }
        val task = Task(
            name = dto.name,
            description = dto.description,
            status = dto.status,
            calendar = calendar,
            category = category
        )

        val created: Task = _taskRepository.save(task)

        info(this, "Created $created in ${System.currentTimeMillis() - _timer} ms")
        return created.toDto()
    }

    @Cacheable(cacheNames = ["taskById"], key = "#id", condition = "#id != null")
    fun getById(id: UUID): TaskDto {
        info(this, "Fetching task with id $id")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val task: Task = _taskRepository.findByIdAndCalendarUserId(id = id, userId = userId)
            .orElseThrow { NoSuchElementException("Task with id $id not found for user $userId") }

        info(this, "Found $task in ${System.currentTimeMillis() - _timer} ms")
        return task.toDto()
    }

    @Cacheable(cacheNames = ["allTasks"], condition = "#result != null")
    fun getAll(): List<TaskDto> {
        info(this, "Fetching all tasks")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val tasks: List<Task> = _taskRepository.findAllByCalendarUserId(userId = userId)

        info(this, "Found $tasks in ${System.currentTimeMillis() - _timer} ms")
        return tasks.map { it.toDto() }
    }

    @Cacheable(cacheNames = ["calendarTasks"], key = "#calendarId", condition = "#calendarId != null")
    fun getAllByCalendarId(calendarId: UUID): List<TaskDto> {
        info(this, "Fetching all tasks for calendar with id $calendarId")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val tasks: List<Task> = _taskRepository.findAllByCalendarIdAndCalendarUserId(calendarId = calendarId, userId = userId)

        info(this, "Found $tasks in ${System.currentTimeMillis() - _timer} ms")
        return tasks.map { it.toDto() }
    }

    @Cacheable(cacheNames = ["categoryTasks"], key = "#categoryId", condition = "#categoryId != null")
    fun getAllByCategoryId(categoryId: UUID): List<TaskDto> {
        info(this, "Fetching all tasks for category with id $categoryId")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val tasks: List<Task> = _taskRepository.findAllByCategoryIdAndCalendarUserId(categoryId = categoryId, userId = userId)

        info(this, "Found $tasks in ${System.currentTimeMillis() - _timer} ms")
        return tasks.map { it.toDto() }
    }

    fun filter(filter: TaskFilterDto): List<TaskDto> {
        info(this, "Filtering tasks with $filter")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val filteredTasks: List<Task> = _taskRepository.filter(
            userId = userId,
            name = filter.name,
            description = filter.description,
            status = filter.status,
            calendarId = filter.calendarId,
            categoryId = filter.categoryId
        )

        info(this, "Found $filteredTasks in ${System.currentTimeMillis() - _timer} ms")
        return filteredTasks.map { it.toDto() }
    }

    @Caching(
        evict = [
            CacheEvict(cacheNames = ["taskById"], key = "#id"),
            CacheEvict(cacheNames = ["allTasks"], allEntries = true),
            CacheEvict(cacheNames = ["calendarTasks"], allEntries = true),
            CacheEvict(cacheNames = ["categoryTasks"], allEntries = true)
        ]
    )
    fun update(id: UUID, dto: TaskDto): TaskDto {
        info(this, "Updating $dto")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val existing: Task = _taskRepository.findByIdAndCalendarUserId(id = id, userId = userId)
            .orElseThrow { NoSuchElementException("Task with id $id not found for user $userId") }
        val calendar: Calendar = dto.calendarId.let {
            _calendarRepository.findByIdAndUserId(id = it, userId = userId).orElseThrow {
                NoSuchElementException("Calendar with id $it not found for user $userId")
            }
        }
        val category: Category? = dto.categoryId?.let {
            _categoryRepository.findByIdAndUserId(id = it, userId = userId).orElseThrow {
                NoSuchElementException("Category with id $it not found for user $userId")
            }
        }
        val changed: Task = existing.copy(
            name = dto.name,
            description = dto.description,
            status = dto.status,
            calendar = calendar,
            category = category
        )

        val updated: Task = _taskRepository.save(changed)

        info(this, "Updated $updated in ${System.currentTimeMillis() - _timer} ms")
        return updated.toDto()
    }

    @Caching(
        evict = [
            CacheEvict(cacheNames = ["taskById"], key = "#id"),
            CacheEvict(cacheNames = ["allTasks"], allEntries = true),
            CacheEvict(cacheNames = ["calendarTasks"], allEntries = true),
            CacheEvict(cacheNames = ["categoryTasks"], allEntries = true)
        ]
    )
    fun delete(id: UUID) {
        info(this, "Deleting task with id $id.")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val task: Task = _taskRepository.findByIdAndCalendarUserId(id = id, userId = userId)
            .orElseThrow { NoSuchElementException("Task with id $id not found for user $userId") }

        _taskRepository.delete(task)
        info(this, "Deleted task $task in ${System.currentTimeMillis() - _timer} ms")
    }

    @Caching(
        evict = [
            CacheEvict(cacheNames = ["taskById"], key = "#id", condition = "#id != null"),
            CacheEvict(cacheNames = ["allTasks"], allEntries = true),
            CacheEvict(cacheNames = ["calendarTasks"], allEntries = true),
            CacheEvict(cacheNames = ["categoryTasks"], allEntries = true)
        ]
    )
    fun deleteAllByCalendarId(calendarId: UUID) {
        info(this, "Deleting all tasks for calendar with id $calendarId.")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val tasks: List<Task> = _taskRepository.findAllByCalendarIdAndCalendarUserId(
            calendarId = calendarId,
            userId = userId
        )

        _taskRepository.deleteAll(tasks)
        info(this, "Deleted all tasks for calendar with id $calendarId in ${System.currentTimeMillis() - _timer} ms")
    }

    @Caching(
        evict = [
            CacheEvict(cacheNames = ["taskById"], key = "#id", condition = "#id != null"),
            CacheEvict(cacheNames = ["allTasks"], allEntries = true),
            CacheEvict(cacheNames = ["calendarTasks"], allEntries = true),
            CacheEvict(cacheNames = ["categoryTasks"], allEntries = true)
        ]
    )
    fun removeCategoryByCategoryId(categoryId: UUID) {
        info(this, "Updating all tasks for category with id $categoryId.")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val tasks: List<Task> = _taskRepository.findAllByCategoryIdAndCalendarUserId(
            categoryId = categoryId,
            userId = userId
        )
        tasks.forEach { task ->
            val withoutCategory = task.copy(category = null)
            _taskRepository.save(withoutCategory)
        }

        info(this, "Updated category to null for all tasks in ${System.currentTimeMillis() - _timer} ms")
    }

}
