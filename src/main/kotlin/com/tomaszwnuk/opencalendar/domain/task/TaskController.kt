/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.task

import com.tomaszwnuk.opencalendar.domain.mapper.PageMapper.toPage
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * REST controller for managing tasks.
 * Provides endpoints for creating, retrieving, updating, and deleting tasks, as well as filtering tasks.
 *
 * @property _taskService The service responsible for task-related operations.
 */
@Suppress("unused")
@RestController
@RequestMapping("/tasks")
class TaskController(private val _taskService: TaskService) {

    /**
     * Creates a new task.
     *
     * @param dto The data transfer object containing the details of the task to be created.
     *
     * @return A `ResponseEntity` containing the created task DTO and an HTTP status of 201 (Created).
     */
    @PostMapping
    fun create(@Valid @RequestBody(required = true) dto: TaskDto): ResponseEntity<TaskDto> {
        val created: TaskDto = _taskService.create(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    /**
     * Retrieves all tasks with pagination.
     *
     * @param pageable The pagination and sorting information.
     *
     * @return A `ResponseEntity` containing a paginated list of task DTOs.
     */
    @GetMapping
    fun getAll(
        @PageableDefault(
            size = 10,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable
    ): ResponseEntity<Page<TaskDto>> {
        val tasks: List<TaskDto> = _taskService.getAll()
        return ResponseEntity.ok(tasks.toPage(pageable))
    }

    /**
     * Retrieves a task by its unique identifier.
     *
     * @param id The unique identifier of the task.
     *
     * @return A `ResponseEntity` containing the task DTO.
     */
    @GetMapping("/{id}")
    fun getById(@PathVariable(name = "id", required = true) id: UUID): ResponseEntity<TaskDto> {
        val task: TaskDto = _taskService.getById(id)
        return ResponseEntity.ok(task)
    }

    /**
     * Filters tasks based on the provided criteria.
     *
     * @param title The title of the task (optional).
     * @param description The description of the task (optional).
     * @param status The status of the task (optional).
     * @param calendarId The unique identifier of the associated calendar (optional).
     * @param categoryId The unique identifier of the associated category (optional).
     * @param pageable The pagination and sorting information.
     *
     * @return A `ResponseEntity` containing a paginated list of filtered task DTOs.
     */
    @GetMapping("/filter")
    fun filter(
        @RequestParam(name = "title", required = false) title: String?,
        @RequestParam(name = "description", required = false) description: String?,
        @RequestParam(name = "status", required = false) status: String?,
        @RequestParam(name = "calendarId", required = false) calendarId: UUID?,
        @RequestParam(name = "categoryId", required = false) categoryId: UUID?,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<TaskDto>> {
        val filter = TaskFilterDto(
            title = title,
            description = description,
            status = status?.let { TaskStatus.valueOf(it) },
            calendarId = calendarId,
            categoryId = categoryId
        )
        val tasks: List<TaskDto> = _taskService.filter(filter)
        return ResponseEntity.ok(tasks.toPage(pageable))
    }

    /**
     * Updates an existing task.
     *
     * @param id The unique identifier of the task to be updated.
     * @param dto The data transfer object containing the updated details of the task.
     *
     * @return A `ResponseEntity` containing the updated task DTO.
     */
    @PutMapping("/{id}")
    fun update(
        @PathVariable(name = "id", required = true) id: UUID,
        @Valid @RequestBody(required = true) dto: TaskDto
    ): ResponseEntity<TaskDto> {
        val updated: TaskDto = _taskService.update(id, dto)
        return ResponseEntity.ok(updated)
    }

    /**
     * Deletes a task by its unique identifier.
     *
     * @param id The unique identifier of the task to be deleted.
     *
     * @return A `ResponseEntity` with an HTTP status of 204 (No Content).
     */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable(name = "id", required = true) id: UUID): ResponseEntity<Void> {
        _taskService.delete(id)
        return ResponseEntity.noContent().build()
    }

}
