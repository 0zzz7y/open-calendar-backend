/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.category

import com.tomaszwnuk.opencalendar.domain.event.EventDto
import com.tomaszwnuk.opencalendar.domain.event.EventService
import com.tomaszwnuk.opencalendar.domain.mapper.ItemTypeMapper.toMapWithType
import com.tomaszwnuk.opencalendar.domain.mapper.PageMapper.toPage
import com.tomaszwnuk.opencalendar.domain.note.NoteDto
import com.tomaszwnuk.opencalendar.domain.note.NoteService
import com.tomaszwnuk.opencalendar.domain.task.TaskDto
import com.tomaszwnuk.opencalendar.domain.task.TaskService
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
 * REST controller for managing categories.
 * Provides endpoints for creating, retrieving, updating, and deleting categories,
 * as well as retrieving related events, tasks, and notes.
 *
 * @property _categoryService The service for managing categories.
 * @property _eventService The service for managing events.
 * @property _taskService The service for managing tasks.
 * @property _noteService The service for managing notes.
 */
@Suppress("unused")
@RestController
@RequestMapping("/categories")
class CategoryController(
    private val _categoryService: CategoryService,
    private val _eventService: EventService,
    private val _taskService: TaskService,
    private val _noteService: NoteService
) {

    /**
     * Creates a new category.
     *
     * @param dto The data transfer object containing category details.
     *
     * @return The created category as a DTO wrapped in a ResponseEntity.
     */
    @PostMapping
    fun create(@Valid @RequestBody(required = true) dto: CategoryDto): ResponseEntity<CategoryDto> {
        val created: CategoryDto = _categoryService.create(dto = dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    /**
     * Retrieves all categories with pagination.
     *
     * @param pageable The pagination and sorting information.
     *
     * @return A paginated list of categories as DTOs wrapped in a ResponseEntity.
     */
    @GetMapping
    fun getAll(
        @PageableDefault(
            size = 10,
            sort = ["title"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable
    ): ResponseEntity<Page<CategoryDto>> {
        val categories: List<CategoryDto> = _categoryService.getAll()
        return ResponseEntity.ok(categories.toPage(pageable = pageable))
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param id The unique identifier of the category.
     *
     * @return The category as a DTO wrapped in a ResponseEntity.
     */
    @GetMapping("/{id}")
    fun getById(@PathVariable(name = "id", required = true) id: UUID): ResponseEntity<CategoryDto> {
        val category: CategoryDto = _categoryService.getById(id = id)
        return ResponseEntity.ok(category)
    }

    /**
     * Retrieves events associated with a specific category.
     *
     * @param id The unique identifier of the category.
     * @param pageable The pagination and sorting information.
     *
     * @return A paginated list of events as DTOs wrapped in a ResponseEntity.
     */
    @GetMapping("/{id}/events")
    fun getEvents(
        @PathVariable(name = "id", required = true) id: UUID,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<EventDto>> {
        val events: List<EventDto> = _eventService.getAllByCategoryId(categoryId = id)
        return ResponseEntity.ok(events.toPage(pageable = pageable))
    }

    /**
     * Retrieves tasks associated with a specific category.
     *
     * @param id The unique identifier of the category.
     * @param pageable The pagination and sorting information.
     *
     * @return A paginated list of tasks as DTOs wrapped in a ResponseEntity.
     */
    @GetMapping("/{id}/tasks")
    fun getTasks(
        @PathVariable(name = "id", required = true) id: UUID,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<TaskDto>> {
        val tasks: List<TaskDto> = _taskService.getAllByCategoryId(categoryId = id)
        return ResponseEntity.ok(tasks.toPage(pageable = pageable))
    }

    /**
     * Retrieves notes associated with a specific category.
     *
     * @param id The unique identifier of the category.
     * @param pageable The pagination and sorting information.
     *
     * @return A paginated list of notes as DTOs wrapped in a ResponseEntity.
     */
    @GetMapping("/{id}/notes")
    fun getNotes(
        @PathVariable(name = "id", required = true) id: UUID,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<NoteDto>> {
        val notes: List<NoteDto> = _noteService.getAllByCategoryId(categoryId = id)
        return ResponseEntity.ok(notes.toPage(pageable = pageable))
    }

    /**
     * Retrieves all items (events, tasks, notes) associated with a specific category.
     *
     * @param id The unique identifier of the category.
     * @param pageable The pagination and sorting information.
     *
     * @return A list of items (as maps) wrapped in a ResponseEntity.
     */
    @GetMapping("/{id}/items")
    fun getAllItems(
        @PathVariable(name = "id", required = true) id: UUID,
        @PageableDefault(
            size = 10,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable
    ): ResponseEntity<List<Map<String, Any>>> {
        val events: Page<Map<String, Any>> = _eventService.getAllByCategoryId(categoryId = id).map {
            it.toMapWithType(type = "event")
        }.toPage(pageable)
        val tasks: Page<Map<String, Any>> = _taskService.getAllByCategoryId(categoryId = id).map {
            it.toMapWithType(type = "task")
        }.toPage(pageable)
        val notes: Page<Map<String, Any>> = _noteService.getAllByCategoryId(categoryId = id).map {
            it.toMapWithType(type = "note")
        }.toPage(pageable)

        val items: List<Map<String, Any>> = events + tasks + notes
        return ResponseEntity.ok(items)
    }

    /**
     * Filters categories based on the provided criteria.
     *
     * @param title The title to filter by (optional).
     * @param color The color to filter by (optional).
     * @param pageable The pagination and sorting information.
     *
     * @return A paginated list of categories matching the filter as DTOs wrapped in a ResponseEntity.
     */
    @GetMapping("/filter")
    fun filter(
        @RequestParam(name = "title", required = false) title: String?,
        @RequestParam(name = "color", required = false) color: String?,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<CategoryDto>> {
        val filter = CategoryFilterDto(
            title = title,
            color = color
        )
        val categories: List<CategoryDto> = _categoryService.filter(filter = filter)
        return ResponseEntity.ok(categories.toPage(pageable = pageable))
    }

    /**
     * Updates an existing category.
     *
     * @param id The unique identifier of the category to update.
     * @param dto The data transfer object containing updated category details.
     *
     * @return The updated category as a DTO wrapped in a ResponseEntity.
     */
    @PutMapping("/{id}")
    fun update(
        @PathVariable(name = "id", required = true) id: UUID,
        @Valid @RequestBody(required = true) dto: CategoryDto
    ): ResponseEntity<CategoryDto> {
        val updated: CategoryDto = _categoryService.update(id = id, dto = dto)
        return ResponseEntity.ok(updated)
    }

    /**
     * Deletes a category by its ID.
     * Also deletes all associated events, tasks, and notes.
     *
     * @param id The unique identifier of the category to delete.
     *
     * @return A ResponseEntity with no content.
     */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable(name = "id", required = true) id: UUID): ResponseEntity<Void> {
        _eventService.removeCategoryByCategoryId(categoryId = id)
        _taskService.removeCategoryByCategoryId(categoryId = id)
        _noteService.removeCategoryByCategoryId(categoryId = id)
        _categoryService.delete(id)
        return ResponseEntity.noContent().build()
    }

}
