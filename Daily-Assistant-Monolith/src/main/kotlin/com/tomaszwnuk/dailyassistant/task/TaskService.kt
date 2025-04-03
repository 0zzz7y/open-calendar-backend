package com.tomaszwnuk.dailyassistant.task

import java.util.*
import kotlin.NoSuchElementException

class TaskService(
    private val _taskRepository: TaskRepository,
    private val _calendarRepository: CalendarRepository,
    private val _categoryRepository: CategoryRepository,
) {

    fun getAll(): List<Task> = _taskRepository.findAll()

    fun getById(id: UUID): Task = _taskRepository.findById(id).orElseThrow {
        NoSuchElementException("Task with id $id could not be found.")
    }

    fun create(dto: TaskDto): Task {
        val calendar = _calendarRepository.findById(dto.calendarId).orElseThrow {
            NoSuchElementException("Calendar with id ${dto.calendarId} could not be found.")
        }
        val category = dto.categoryId?.let {
            _categoryRepository.findById(it).orElseThrow {
                NoSuchElementException("Category with id $it could not be found.")
            }
        }

        val task = Task(
            title = dto.title,
            description = dto.description,
            date = dto.date,
            recurringPattern = dto.recurringPattern,
            status = dto.status,
            calendar = calendar,
            category = category
        )

        return _taskRepository.save(task)
    }

    fun update(id: UUID, dto: TaskDto): Task {
        val existing = getById(id)
        val calendar = _calendarRepository.findById(dto.calendarId).orElseThrow {
            NoSuchElementException("Calendar with id ${dto.calendarId} could not be found.")
        }
        val category = dto.categoryId?.let {
            _categoryRepository.findById(it).orElseThrow {
                NoSuchElementException("Category with id $it could not be found.")
            }
        }

        val updated = existing.copy(
            title = dto.title,
            description = dto.description,
            date = dto.date,
            recurringPattern = dto.recurringPattern,
            status = dto.status,
            calendar = calendar,
            category = category
        )

        return _taskRepository.save(updated)
    }

    fun delete(id: UUID) {
        val task = getById(id)
        _taskRepository.delete(task)
    }
}
