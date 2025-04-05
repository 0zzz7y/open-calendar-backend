package com.tomaszwnuk.dailyassistant.task

import com.tomaszwnuk.dailyassistant.calendar.Calendar
import com.tomaszwnuk.dailyassistant.calendar.CalendarRepository
import com.tomaszwnuk.dailyassistant.category.Category
import com.tomaszwnuk.dailyassistant.category.CategoryRepository
import com.tomaszwnuk.dailyassistant.domain.info
import com.tomaszwnuk.dailyassistant.domain.validation.findOrThrow
import org.springframework.stereotype.Service
import java.util.*

@Service
class TaskService(
    private val _taskRepository: TaskRepository,
    private val _calendarRepository: CalendarRepository,
    private val _categoryRepository: CategoryRepository,
) {

    fun getAll(): List<Task> {
        info(this, "Fetching all tasks")
        val tasks: List<Task> = _taskRepository.findAll()

        info(this, "Found $tasks")
        return tasks
    }

    fun getById(id: UUID): Task {
        info(this, "Fetching task with id $id")
        val task: Task = _taskRepository.findOrThrow(id)

        info(this, "Found $task")
        return task
    }

    fun create(dto: TaskDto): Task {
        info(this, "Creating $dto")
        val calendar: Calendar? = dto.calendarId?.let { _calendarRepository.findOrThrow(id = it) }
        val category: Category? = dto.categoryId?.let { _categoryRepository.findOrThrow(id = it) }

        val task = Task(
            name = dto.name,
            description = dto.description,
            date = dto.date,
            recurringPattern = dto.recurringPattern,
            status = dto.status,
            calendar = calendar,
            category = category
        )

        info(this, "Created $task")
        return _taskRepository.save(task)
    }

    fun update(id: UUID, dto: TaskDto): Task {
        info(this, "Updating $dto")
        val existing: Task = getById(id)
        val calendar: Calendar? = dto.calendarId?.let { _calendarRepository.findOrThrow(id = it) }
        val category: Category? = dto.categoryId?.let { _categoryRepository.findOrThrow(id = it) }

        val updated: Task = existing.copy(
            name = dto.name,
            description = dto.description,
            date = dto.date,
            recurringPattern = dto.recurringPattern,
            status = dto.status,
            calendar = calendar,
            category = category
        )

        info(this, "Updated $updated")
        return _taskRepository.save(updated)
    }

    fun delete(id: UUID) {
        info(this, "Deleting task with id $id.")
        val task: Task = getById(id)

        info(this, "Deleting task $task")
        _taskRepository.delete(task)
    }

}
