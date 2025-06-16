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

/**
 * The service for task operations.
 */
@Service
class TaskService(

    /**
     * The repository for managing task data.
     */
    private val _taskRepository: TaskRepository,

    /**
     * The repository for managing calendar data.
     */
    private val _calendarRepository: CalendarRepository,

    /**
     * The repository for managing category data.
     */
    private val _categoryRepository: CategoryRepository,

    /**
     * The service for user operations.
     */
    private val _userService: UserService

) {

    /**
     * The timer for measuring the duration of operations.
     */
    private var _timer: Long = 0

    /**
     * Creates a new task.
     *
     * @param dto The data transfer object containing task details
     *
     * @return The created task as a data transfer object
     *
     * @throws NoSuchElementException if the calendar or category with the specified IDs does not exist for the user
     */
    @Caching(
        evict = [
            CacheEvict(cacheNames = ["allTasks"], allEntries = true),
            CacheEvict(cacheNames = ["calendarTasks"], allEntries = true),
            CacheEvict(cacheNames = ["categoryTasks"], allEntries = true)
        ]
    )
    fun create(dto: TaskDto): TaskDto {
        info(source = this, message = "Creating $dto")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val calendar: Optional<Calendar> = _calendarRepository.findByIdAndUserId(id = dto.calendarId, userId = userId)
        if (calendar.isEmpty) {
            throw IllegalArgumentException("Calendar with id ${dto.calendarId} not found for user $userId")
        }

        val category: Optional<Category>? = dto.categoryId?.let {
            _categoryRepository.findByIdAndUserId(id = it, userId = userId)
        }

        val task = Task(
            name = dto.name,
            description = dto.description,
            status = dto.status,
            calendar = calendar.get(),
            category = category?.get()
        )

        val created: Task = _taskRepository.save(task)
        info(source = this, message = "Created $created in ${System.currentTimeMillis() - _timer} ms")
        return created.toDto()
    }

    /**
     * Retrieves a task by its unique identifier.
     *
     * @param id The unique identifier of the task
     *
     * @return The task as a data transfer object
     *
     * @throws NoSuchElementException if the task with the specified unique identifier does not exist for the user
     */
    @Cacheable(cacheNames = ["taskById"], key = "#id", condition = "#id != null")
    fun getById(id: UUID): TaskDto {
        info(source = this, message = "Fetching task with id $id")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val task: Optional<Task> = _taskRepository.findByIdAndCalendarUserId(id = id, userId = userId)
        if (task.isEmpty) {
            throw NoSuchElementException("Task with id $id not found for user $userId")
        }

        info(source = this, message = "Found $task in ${System.currentTimeMillis() - _timer} ms")
        return task.get().toDto()
    }

    /**
     * Retrieves all tasks for the current user.
     *
     * @return A list of all tasks as data transfer objects
     */
    @Cacheable(cacheNames = ["allTasks"], condition = "#result != null")
    fun getAll(): List<TaskDto> {
        info(source = this, message = "Fetching all tasks")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val tasks: List<Task> = _taskRepository.findAllByCalendarUserId(userId = userId)

        info(source = this, message = "Found $tasks in ${System.currentTimeMillis() - _timer} ms")
        return tasks.map { it.toDto() }
    }

    /**
     * Retrieves all tasks associated with a specific calendar.
     *
     * @param calendarId The unique identifier of the calendar
     *
     * @return A list of tasks associated with the specified calendar as data transfer objects
     */
    @Cacheable(cacheNames = ["calendarTasks"], key = "#calendarId", condition = "#calendarId != null")
    fun getAllByCalendarId(calendarId: UUID): List<TaskDto> {
        info(source = this, message = "Fetching all tasks for calendar with id $calendarId")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val tasks: List<Task> = _taskRepository.findAllByCalendarIdAndCalendarUserId(
            calendarId = calendarId,
            userId = userId
        )

        info(source = this, message = "Found $tasks in ${System.currentTimeMillis() - _timer} ms")
        return tasks.map { it.toDto() }
    }

    /**
     * Retrieves all tasks associated with a specific category.
     *
     * @param categoryId The unique identifier of the category
     *
     * @return A list of tasks associated with the specified category as data transfer objects
     */
    @Cacheable(cacheNames = ["categoryTasks"], key = "#categoryId", condition = "#categoryId != null")
    fun getAllByCategoryId(categoryId: UUID): List<TaskDto> {
        info(source = this, message = "Fetching all tasks for category with id $categoryId")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val tasks: List<Task> = _taskRepository.findAllByCategoryIdAndCalendarUserId(
            categoryId = categoryId,
            userId = userId
        )

        info(source = this, message = "Found $tasks in ${System.currentTimeMillis() - _timer} ms")
        return tasks.map { it.toDto() }
    }

    /**
     * Filters tasks based on the provided criteria.
     *
     * @param filter The filter criteria as a data transfer object
     *
     * @return A list of tasks that match the filter criteria as data transfer objects
     */
    fun filter(filter: TaskFilterDto): List<TaskDto> {
        info(source = this, message = "Filtering tasks with $filter")
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

        info(source = this, message = "Found $filteredTasks in ${System.currentTimeMillis() - _timer} ms")
        return filteredTasks.map { it.toDto() }
    }

    /**
     * Updates an existing task.
     *
     * @param id The unique identifier of the task to update
     * @param dto The data transfer object containing updated task details
     *
     * @return The updated task as a data transfer object
     *
     * @throws NoSuchElementException if the task with the specified ID does not exist for the user
     */
    @Caching(
        evict = [
            CacheEvict(cacheNames = ["taskById"], key = "#id"),
            CacheEvict(cacheNames = ["allTasks"], allEntries = true),
            CacheEvict(cacheNames = ["calendarTasks"], allEntries = true),
            CacheEvict(cacheNames = ["categoryTasks"], allEntries = true)
        ]
    )
    fun update(id: UUID, dto: TaskDto): TaskDto {
        info(source = this, message = "Updating $dto")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val existing: Optional<Task> = _taskRepository.findByIdAndCalendarUserId(id = id, userId = userId)
        if (existing.isEmpty) {
            IllegalArgumentException("Event with id $id not found for user $userId")
        }

        val calendar: Optional<Calendar> = _calendarRepository.findByIdAndUserId(id = dto.calendarId, userId = userId)
        if (calendar.isEmpty) {
            throw IllegalArgumentException("Calendar with id ${dto.calendarId} not found for user $userId")
        }

        val category: Optional<Category>? = dto.categoryId?.let {
            _categoryRepository.findByIdAndUserId(id = it, userId = userId)
        }

        val changed: Task = existing.get().copy(
            name = dto.name,
            description = dto.description,
            status = dto.status,
            calendar = calendar.get(),
            category = category?.get()
        )

        val updated: Task = _taskRepository.save(changed)

        info(source = this, message = "Updated $updated in ${System.currentTimeMillis() - _timer} ms")
        return updated.toDto()
    }

    /**
     * Deletes a task by its unique identifier.
     *
     * @param id The unique identifier of the task to delete
     *
     * @throws NoSuchElementException if the task with the specified ID does not exist for the user
     */
    @Caching(
        evict = [
            CacheEvict(cacheNames = ["taskById"], key = "#id"),
            CacheEvict(cacheNames = ["allTasks"], allEntries = true),
            CacheEvict(cacheNames = ["calendarTasks"], allEntries = true),
            CacheEvict(cacheNames = ["categoryTasks"], allEntries = true)
        ]
    )
    fun delete(id: UUID) {
        info(source = this, message = "Deleting task with id $id.")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val existing: Optional<Task> = _taskRepository.findByIdAndCalendarUserId(id = id, userId = userId)
        if (existing.isEmpty) {
            IllegalArgumentException("Event with id $id not found for user $userId")
        }

        _taskRepository.delete(existing.get())
        info(source = this, message = "Deleted task $existing in ${System.currentTimeMillis() - _timer} ms")
    }

    /**
     * Deletes all tasks associated with a specific calendar.
     *
     * @param calendarId The unique identifier of the calendar
     *
     * @throws NoSuchElementException if no tasks are found for the specified calendar
     */
    @Caching(
        evict = [
            CacheEvict(cacheNames = ["taskById"], key = "#id", condition = "#id != null"),
            CacheEvict(cacheNames = ["allTasks"], allEntries = true),
            CacheEvict(cacheNames = ["calendarTasks"], allEntries = true),
            CacheEvict(cacheNames = ["categoryTasks"], allEntries = true)
        ]
    )
    fun deleteAllByCalendarId(calendarId: UUID) {
        info(source = this, message = "Deleting all tasks for calendar with id $calendarId.")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val tasks: List<Task> = _taskRepository.findAllByCalendarIdAndCalendarUserId(
            calendarId = calendarId,
            userId = userId
        )

        _taskRepository.deleteAll(tasks)
        info(
            source = this,
            message = "Deleted all tasks for calendar with id $calendarId in ${System.currentTimeMillis() - _timer} ms"
        )
    }

    /**
     * Removes the category from all tasks associated with a specific category.
     *
     * @param categoryId The unique identifier of the category
     *
     * @throws NoSuchElementException if no tasks are found for the specified category
     */
    @Caching(
        evict = [
            CacheEvict(cacheNames = ["taskById"], key = "#id", condition = "#id != null"),
            CacheEvict(cacheNames = ["allTasks"], allEntries = true),
            CacheEvict(cacheNames = ["calendarTasks"], allEntries = true),
            CacheEvict(cacheNames = ["categoryTasks"], allEntries = true)
        ]
    )
    fun removeCategoryByCategoryId(categoryId: UUID) {
        info(source = this, message = "Updating all tasks for category with id $categoryId.")
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

        info(
            source = this,
            message = "Updated category to null for all tasks in ${System.currentTimeMillis() - _timer} ms"
        )
    }

}
