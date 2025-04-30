/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.task

import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.calendar.CalendarRepository
import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.category.CategoryRepository
import com.tomaszwnuk.opencalendar.utility.logger.info
import com.tomaszwnuk.opencalendar.utility.validation.findOrThrow
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import java.util.*

/**
 * Service class for managing tasks.
 * Provides methods for creating, retrieving, updating, deleting, and filtering tasks.
 *
 * @property _taskRepository Repository for performing CRUD operations on tasks.
 * @property _calendarRepository Repository for accessing calendar entities.
 * @property _categoryRepository Repository for accessing category entities.
 */
@Service
class TaskService(
    private val _taskRepository: TaskRepository,
    private val _calendarRepository: CalendarRepository,
    private val _categoryRepository: CategoryRepository
) {

    /**
     * Timer used for measuring execution time of operations.
     */
    private var _timer: Long = 0

    /**
     * Creates a new task and saves it to the repository.
     * Evicts relevant caches after the task is created.
     *
     * @param dto The data transfer object containing task details.
     *
     * @return The created task as a DTO.
     */
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

        val calendar: Calendar = dto.calendarId.let { _calendarRepository.findOrThrow(id = it) }
        val category: Category? = dto.categoryId?.let { _categoryRepository.findOrThrow(id = it) }
        val task = Task(
            title = dto.title,
            description = dto.description,
            status = dto.status,
            calendar = calendar,
            category = category
        )

        val created: Task = _taskRepository.save(task)

        info(this, "Created $created in ${System.currentTimeMillis() - _timer} ms")
        return created.toDto()
    }

    /**
     * Retrieves a task by its unique identifier.
     * Caches the result for future requests.
     *
     * @param id The unique identifier of the task.
     *
     * @return The task as a DTO.
     */
    @Cacheable(cacheNames = ["taskById"], key = "#id", condition = "#id != null")
    fun getById(id: UUID): TaskDto {
        info(this, "Fetching task with id $id")
        _timer = System.currentTimeMillis()

        val task: Task = _taskRepository.findOrThrow(id)

        info(this, "Found $task in ${System.currentTimeMillis() - _timer} ms")
        return task.toDto()
    }

    /**
     * Retrieves all tasks from the repository.
     * Caches the result for future requests.
     *
     * @return A list of all tasks as DTOs.
     */
    @Cacheable(cacheNames = ["allTasks"], condition = "#result != null")
    fun getAll(): List<TaskDto> {
        info(this, "Fetching all tasks")
        _timer = System.currentTimeMillis()

        val tasks: List<Task> = _taskRepository.findAll()

        info(this, "Found $tasks in ${System.currentTimeMillis() - _timer} ms")
        return tasks.map { it.toDto() }
    }

    /**
     * Retrieves all tasks associated with a specific calendar.
     * Caches the result for future requests.
     *
     * @param calendarId The unique identifier of the calendar.
     *
     * @return A list of tasks associated with the calendar as DTOs.
     */
    @Cacheable(cacheNames = ["calendarTasks"], key = "#calendarId", condition = "#calendarId != null")
    fun getAllByCalendarId(calendarId: UUID): List<TaskDto> {
        info(this, "Fetching all tasks for calendar with id $calendarId")
        _timer = System.currentTimeMillis()

        val tasks: List<Task> = _taskRepository.findAllByCalendarId(calendarId)

        info(this, "Found $tasks in ${System.currentTimeMillis() - _timer} ms")
        return tasks.map { it.toDto() }
    }

    /**
     * Retrieves all tasks associated with a specific category.
     * Caches the result for future requests.
     *
     * @param categoryId The unique identifier of the category.
     *
     * @return A list of tasks associated with the category as DTOs.
     */
    @Cacheable(cacheNames = ["categoryTasks"], key = "#categoryId", condition = "#categoryId != null")
    fun getAllByCategoryId(categoryId: UUID): List<TaskDto> {
        info(this, "Fetching all tasks for category with id $categoryId")
        _timer = System.currentTimeMillis()

        val tasks: List<Task> = _taskRepository.findAllByCategoryId(categoryId)

        info(this, "Found $tasks in ${System.currentTimeMillis() - _timer} ms")
        return tasks.map { it.toDto() }
    }

    /**
     * Filters tasks based on the provided criteria.
     *
     * @param filter The filter criteria encapsulated in a DTO.
     *
     * @return A list of tasks matching the filter criteria as DTOs.
     */
    fun filter(filter: TaskFilterDto): List<TaskDto> {
        info(this, "Filtering tasks with $filter")
        _timer = System.currentTimeMillis()

        val filteredTasks: List<Task> = _taskRepository.filter(
            title = filter.title,
            description = filter.description,
            status = filter.status,
            calendarId = filter.calendarId,
            categoryId = filter.categoryId
        )

        info(this, "Found $filteredTasks in ${System.currentTimeMillis() - _timer} ms")
        return filteredTasks.map { it.toDto() }
    }

    /**
     * Updates an existing task with new details.
     * Evicts relevant caches after the task is updated.
     *
     * @param id The unique identifier of the task to update.
     * @param dto The data transfer object containing updated task details.
     *
     * @return The updated task as a DTO.
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
        info(this, "Updating $dto")
        _timer = System.currentTimeMillis()

        val existing: Task = _taskRepository.findOrThrow(id = id)
        val calendar: Calendar = dto.calendarId.let { _calendarRepository.findOrThrow(id = it) }
        val category: Category? = dto.categoryId?.let { _categoryRepository.findOrThrow(id = it) }
        val changed: Task = existing.copy(
            title = dto.title,
            description = dto.description,
            status = dto.status,
            calendar = calendar,
            category = category
        )

        val updated: Task = _taskRepository.save(changed)

        info(this, "Updated $updated in ${System.currentTimeMillis() - _timer} ms")
        return updated.toDto()
    }

    /**
     * Deletes a task by its unique identifier.
     * Evicts relevant caches after the task is deleted.
     *
     * @param id The unique identifier of the task to delete.
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
        info(this, "Deleting task with id $id.")
        _timer = System.currentTimeMillis()

        val task: Task = _taskRepository.findOrThrow(id = id)

        _taskRepository.delete(task)
        info(this, "Deleted task $task in ${System.currentTimeMillis() - _timer} ms")
    }

    /**
     * Deletes all tasks associated with a specific calendar.
     * Evicts relevant caches after the tasks are deleted.
     *
     * @param calendarId The unique identifier of the calendar.
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
        info(this, "Deleting all tasks for calendar with id $calendarId.")
        _timer = System.currentTimeMillis()

        val tasks: List<Task> = _taskRepository.findAllByCalendarId(calendarId = calendarId)

        _taskRepository.deleteAll(tasks)
        info(this, "Deleted all tasks for calendar with id $calendarId in ${System.currentTimeMillis() - _timer} ms")
    }

    /**
     * Removes the category association from all tasks associated with a specific category.
     * Evicts relevant caches after the tasks are updated.
     *
     * @param categoryId The unique identifier of the category.
     */
    @Caching(
        evict = [
            CacheEvict(cacheNames = ["taskById"], key = "#id", condition = "#id != null"),
            CacheEvict(cacheNames = ["allTasks"], allEntries = true),
            CacheEvict(cacheNames = ["calendarTasks"], allEntries = true),
            CacheEvict(cacheNames = ["categoryTasks"], allEntries = true)
        ]
    )
    fun deleteAllCategoryByCategoryId(categoryId: UUID) {
        info(this, "Updating all tasks for category with id $categoryId.")
        _timer = System.currentTimeMillis()

        val tasks: List<Task> = _taskRepository.findAllByCategoryId(categoryId)
        tasks.forEach { task ->
            val withoutCategory = task.copy(category = null)
            _taskRepository.save(withoutCategory)
        }

        info(this, "Updated category to null for all tasks in ${System.currentTimeMillis() - _timer} ms")
    }

}
