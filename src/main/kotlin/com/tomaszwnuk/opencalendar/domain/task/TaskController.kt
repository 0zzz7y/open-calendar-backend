package com.tomaszwnuk.opencalendar.domain.task

import com.tomaszwnuk.opencalendar.domain.communication.CommunicationConstants
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
 * The controller for managing tasks.
 */
@Suppress("unused")
@RestController
@RequestMapping("/${CommunicationConstants.API}/${CommunicationConstants.API_VERSION}/tasks")
class TaskController(

    /**
     * The service for performing operations on tasks.
     */
    private val _taskService: TaskService

) {

    /**
     * Creates a new task.
     *
     * @param dto The data transfer object containing the details of the task
     *
     * @return A response containing the created task
     */
    @PostMapping
    fun create(@Valid @RequestBody(required = true) dto: TaskDto): ResponseEntity<TaskDto> {
        val created: TaskDto = _taskService.create(dto = dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    /**
     * Retrieves all tasks.
     *
     * @param pageable The pagination information
     *
     * @return A response containing a page of tasks
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
        return ResponseEntity.ok(tasks.toPage(pageable = pageable))
    }

    /**
     * Retrieves a task by its unique identifier.
     *
     * @param id The unique identifier of the task
     *
     * @return A response containing the task with the specified identifier
     */
    @GetMapping("/{id}")
    fun getById(@PathVariable(name = "id", required = true) id: UUID): ResponseEntity<TaskDto> {
        val task: TaskDto = _taskService.getById(id = id)
        return ResponseEntity.ok(task)
    }

    /**
     * Filters tasks based on the provided criteria.
     *
     * @param name The name of the task (optional)
     * @param description The description of the task (optional)
     * @param status The status of the task (optional)
     * @param calendarId The unique identifier of the calendar associated with the task (optional)
     * @param categoryId The unique identifier of the category associated with the task (optional)
     * @param pageable The pagination information
     *
     * @return A response containing a page of filtered tasks
     */
    @GetMapping("/filter")
    fun filter(
        @RequestParam(name = "name", required = false) name: String?,
        @RequestParam(name = "description", required = false) description: String?,
        @RequestParam(name = "status", required = false) status: String?,
        @RequestParam(name = "calendarId", required = false) calendarId: UUID?,
        @RequestParam(name = "categoryId", required = false) categoryId: UUID?,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<TaskDto>> {
        val filter = TaskFilterDto(
            name = name,
            description = description,
            status = status?.let { TaskStatus.valueOf(it) },
            calendarId = calendarId,
            categoryId = categoryId
        )
        val tasks: List<TaskDto> = _taskService.filter(filter = filter)
        return ResponseEntity.ok(tasks.toPage(pageable = pageable))
    }

    /**
     * Updates an existing task.
     *
     * @param id The unique identifier of the task to update
     * @param dto The data transfer object containing the updated details of the task
     *
     * @return A response containing the updated task
     */
    @PutMapping("/{id}")
    fun update(
        @PathVariable(name = "id", required = true) id: UUID,
        @Valid @RequestBody(required = true) dto: TaskDto
    ): ResponseEntity<TaskDto> {
        val updated: TaskDto = _taskService.update(id = id, dto = dto)
        return ResponseEntity.ok(updated)
    }

    /**
     * Deletes a task by its unique identifier.
     *
     * @param id The unique identifier of the task to delete
     *
     * @return A response indicating the deletion status
     */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable(name = "id", required = true) id: UUID): ResponseEntity<Void> {
        _taskService.delete(id = id)
        return ResponseEntity.noContent().build()
    }

}
