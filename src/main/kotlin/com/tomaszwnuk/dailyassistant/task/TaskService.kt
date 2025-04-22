package com.tomaszwnuk.dailyassistant.task

import com.tomaszwnuk.dailyassistant.calendar.Calendar
import com.tomaszwnuk.dailyassistant.calendar.CalendarRepository
import com.tomaszwnuk.dailyassistant.category.Category
import com.tomaszwnuk.dailyassistant.category.CategoryRepository
import com.tomaszwnuk.dailyassistant.domain.utility.info
import com.tomaszwnuk.dailyassistant.validation.findOrThrow
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class TaskService(
    private val _taskRepository: TaskRepository,
    private val _calendarRepository: CalendarRepository,
    private val _categoryRepository: CategoryRepository
) {

    private var _timer: Long = 0

    @Caching(evict = [
        CacheEvict(cacheNames = ["calendarTasks"], allEntries = true)
    ])
    fun create(dto: TaskDto): Task {
        info(this, "Creating $dto")
        _timer = System.currentTimeMillis()
        val calendar: Calendar = dto.calendarId.let { _calendarRepository.findOrThrow(id = it) }
        val category: Category? = dto.categoryId?.let { _categoryRepository.findOrThrow(id = it) }
        val task = Task(
            name = dto.name,
            description = dto.description,
            startDate = dto.startDate,
            endDate = dto.endDate,
            recurringPattern = dto.recurringPattern,
            status = dto.status,
            calendar = calendar,
            category = category
        )

        val created: Task = _taskRepository.save(task)
        info(this, "Created $created in ${System.currentTimeMillis() - _timer} ms")

        return created
    }

    @Cacheable(cacheNames = ["taskById"], key = "#id")
    fun getById(id: UUID): Task {
        info(this, "Fetching task with id $id")
        _timer = System.currentTimeMillis()
        val task: Task = _taskRepository.findOrThrow(id)

        info(this, "Found $task in ${System.currentTimeMillis() - _timer} ms")
        return task
    }

    fun getAll(pageable: Pageable): Page<Task> {
        info(this, "Fetching all tasks")
        _timer = System.currentTimeMillis()
        val tasks: Page<Task> = _taskRepository.findAll(pageable)

        info(this, "Found $tasks in ${System.currentTimeMillis() - _timer} ms")
        return tasks
    }

    @Cacheable(cacheNames = ["calendarTasks"], key = "#calendarId")
    fun getAllByCalendarId(calendarId: UUID, pageable: Pageable): Page<Task> {
        info(this, "Fetching all tasks for calendar with id $calendarId")
        _timer = System.currentTimeMillis()
        val tasks: Page<Task> = _taskRepository.findAllByCalendarId(calendarId, pageable)

        info(this, "Found $tasks in ${System.currentTimeMillis() - _timer} ms")
        return tasks
    }

    @Cacheable(cacheNames = ["categoryTasks"], key = "#categoryId")
    fun getAllByCategoryId(categoryId: UUID, pageable: Pageable): Page<Task> {
        info(this, "Fetching all tasks for calendar with id $categoryId")
        _timer = System.currentTimeMillis()
        val tasks: Page<Task> = _taskRepository.findAllByCategoryId(categoryId, pageable)

        info(this, "Found $tasks in ${System.currentTimeMillis() - _timer} ms")
        return tasks
    }

    fun filter(filter: TaskFilterDto, pageable: Pageable): Page<Task> {
        info(this, "Filtering tasks with $filter")
        _timer = System.currentTimeMillis()
        val filteredTasks: Page<Task> = _taskRepository.filter(
            name = filter.name,
            description = filter.description,
            dateFrom = filter.dateFrom,
            dateTo = filter.dateTo,
            recurringPattern = filter.recurringPattern,
            status = filter.status,
            calendarId = filter.calendarId,
            categoryId = filter.categoryId,
            pageable = pageable
        )

        info(this, "Found $filteredTasks in ${System.currentTimeMillis() - _timer} ms")
        return filteredTasks
    }

    @Caching(evict = [
        CacheEvict(cacheNames = ["calendarTasks"], key = "#dto.calendarId"),
        CacheEvict(cacheNames = ["taskById"], key = "#id")
    ])
    fun update(id: UUID, dto: TaskDto): Task {
        info(this, "Updating $dto")
        _timer = System.currentTimeMillis()
        val existing: Task = getById(id)
        val calendar: Calendar = dto.calendarId.let { _calendarRepository.findOrThrow(id = it) }
        val category: Category? = dto.categoryId?.let { _categoryRepository.findOrThrow(id = it) }
        val changed: Task = existing.copy(
            name = dto.name,
            description = dto.description,
            startDate = dto.startDate,
            endDate = dto.endDate,
            recurringPattern = dto.recurringPattern,
            status = dto.status,
            calendar = calendar,
            category = category
        )

        val updated: Task = _taskRepository.save(changed)
        info(this, "Updated $updated in ${System.currentTimeMillis() - _timer} ms")

        return updated
    }

    @Caching(evict = [
        CacheEvict(cacheNames = ["calendarTasks"], allEntries = true),
        CacheEvict(cacheNames = ["taskById"], key = "#id")
    ])
    fun delete(id: UUID) {
        info(this, "Deleting task with id $id.")
        _timer = System.currentTimeMillis()
        val task: Task = getById(id)

        _taskRepository.delete(task)
        info(this, "Deleted task $task in ${System.currentTimeMillis() - _timer} ms")
    }

    @Caching(evict = [
        CacheEvict(cacheNames = ["calendarTasks"], key = "#calendarId"),
        CacheEvict(cacheNames = ["taskById"], allEntries = true)
    ])
    fun deleteAllByCalendarId(calendarId: UUID) {
        info(this, "Deleting all tasks for calendar with id $calendarId.")
        _timer = System.currentTimeMillis()
        val tasks: Page<Task> = _taskRepository.findAllByCalendarId(
            calendarId = calendarId,
            pageable = Pageable.unpaged()
        )

        _taskRepository.deleteAll(tasks)
        info(this, "Deleted all tasks for calendar with id $calendarId in ${System.currentTimeMillis() - _timer} ms")
    }

    @Caching(evict = [
        CacheEvict(cacheNames = ["categoryTasks"], key = "#categoryId"),
        CacheEvict(cacheNames = ["taskById"], allEntries = true)
    ])
    fun deleteAllCategoryByCategoryId(categoryId: UUID) {
        info(this, "Updating all tasks for category with id $categoryId.")
        _timer = System.currentTimeMillis()
        val tasks: Page<Task> = _taskRepository.findAllByCategoryId(
            categoryId = categoryId,
            pageable = Pageable.unpaged()
        )

        tasks.content.forEach { task ->
            val withoutCategory = task.copy(category = null)
            _taskRepository.save(withoutCategory)
        }

        info(this, "Updated category to null for all tasks in ${System.currentTimeMillis() - _timer} ms")
    }

}
