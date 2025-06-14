package com.tomaszwnuk.opencalendar.domain.category

import com.tomaszwnuk.opencalendar.domain.communication.CommunicationConstants
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
 * The controller for managing categories.
 */
@Suppress("unused")
@RestController
@RequestMapping("/${CommunicationConstants.API}/${CommunicationConstants.API_VERSION}/categories")
class CategoryController(

    /**
     * The service for performing operations on categories.
     */
    private val _categoryService: CategoryService,

    /**
     * The service for performing operations on events.
     */
    private val _eventService: EventService,

    /**
     * The service for performing operations on tasks.
     */
    private val _taskService: TaskService,

    /**
     * The service for performing operations on notes.
     */
    private val _noteService: NoteService

) {

    /**
     * Creates a new category.
     *
     * @param dto The data transfer object containing the details of the category
     *
     * @return A response containing the created category
     */
    @PostMapping
    fun create(@Valid @RequestBody(required = true) dto: CategoryDto): ResponseEntity<CategoryDto> {
        val created: CategoryDto = _categoryService.create(dto = dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    /**
     * Retrieves all categories.
     *
     * @param pageable The pagination information
     *
     * @return A response containing a page of categories
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
     * Retrieves a category by its unique identifier.
     *
     * @param id The unique identifier of the category
     *
     * @return A response containing the category
     */
    @GetMapping("/{id}")
    fun getById(@PathVariable(name = "id", required = true) id: UUID): ResponseEntity<CategoryDto> {
        val category: CategoryDto = _categoryService.getById(id = id)
        return ResponseEntity.ok(category)
    }

    /**
     * Retrieves all events associated with a category.
     *
     * @param id The unique identifier of the category
     * @param pageable The pagination information
     *
     * @return A response containing a page of events associated with the category
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
     * Retrieves all tasks associated with a category.
     *
     * @param id The unique identifier of the category
     * @param pageable The pagination information
     *
     * @return A response containing a page of tasks associated with the category
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
     * Retrieves all notes associated with a category.
     *
     * @param id The unique identifier of the category
     * @param pageable The pagination information
     *
     * @return A response containing a page of notes associated with the category
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
     * Retrieves all items associated with a category.
     *
     * @param id The unique identifier of the category
     * @param pageable The pagination information
     *
     * @return A response containing a list of items associated with the category
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
     * Retrieves filtered categories based on the provided criteria.
     *
     * @param name The name of the category to filter by (optional)
     * @param color The color of the category to filter by (optional)
     * @param pageable The pagination information
     *
     * @return A response containing a page of filtered categories
     */
    @GetMapping("/filter")
    fun filter(
        @RequestParam(name = "name", required = false) name: String?,
        @RequestParam(name = "color", required = false) color: String?,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<CategoryDto>> {
        val filter = CategoryFilterDto(
            name = name,
            color = color
        )
        val categories: List<CategoryDto> = _categoryService.filter(filter = filter)
        return ResponseEntity.ok(categories.toPage(pageable = pageable))
    }

    /**
     * Updates an existing category.
     *
     * @param id The unique identifier of the category to update
     * @param dto The data transfer object containing the updated details of the category
     *
     * @return A response containing the updated category
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
     * Deletes a category by its unique identifier.
     *
     * @param id The unique identifier of the category to delete
     *
     * @return A response indicating the deletion status
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
