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

@Suppress("unused")
@RestController
@RequestMapping("/categories")
class CategoryController(
    private val _categoryService: CategoryService,
    private val _eventService: EventService,
    private val _taskService: TaskService,
    private val _noteService: NoteService
) {

    @PostMapping
    fun create(@Valid @RequestBody dto: CategoryDto): ResponseEntity<CategoryDto> {
        val created: CategoryDto = _categoryService.create(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @GetMapping
    fun getAll(
        @PageableDefault(
            size = 10,
            sort = ["title"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable
    ): ResponseEntity<Page<CategoryDto>> {
        val categories: List<CategoryDto> = _categoryService.getAll()
        return ResponseEntity.ok(categories.toPage(pageable))
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<CategoryDto> {
        val category: CategoryDto = _categoryService.getById(id)
        return ResponseEntity.ok(category)
    }

    @GetMapping("/{id}/events")
    fun getEvents(
        @PathVariable id: UUID,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<EventDto>> {
        val events: List<EventDto> = _eventService.getAllByCategoryId(id)
        return ResponseEntity.ok(events.toPage(pageable))
    }

    @GetMapping("/{id}/tasks")
    fun getTasks(
        @PathVariable id: UUID,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<TaskDto>> {
        val tasks: List<TaskDto> = _taskService.getAllByCategoryId(id)
        return ResponseEntity.ok(tasks.toPage(pageable))
    }

    @GetMapping("/{id}/notes")
    fun getNotes(
        @PathVariable id: UUID,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<NoteDto>> {
        val notes: List<NoteDto> = _noteService.getAllByCategoryId(id)
        return ResponseEntity.ok(notes.toPage(pageable))
    }

    @GetMapping("/{id}/items")
    fun getAllItems(
        @PathVariable id: UUID,
        @PageableDefault(
            size = 10,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable
    ): ResponseEntity<List<Map<String, Any>>> {
        val events: Page<Map<String, Any>> = _eventService.getAllByCategoryId(id).map {
            it.toMapWithType("event")
        }.toPage(pageable)
        val tasks: Page<Map<String, Any>> = _taskService.getAllByCategoryId(id).map {
            it.toMapWithType("task")
        }.toPage(pageable)
        val notes: Page<Map<String, Any>> = _noteService.getAllByCategoryId(id).map {
            it.toMapWithType("note")
        }.toPage(pageable)

        val items: List<Map<String, Any>> = events + tasks + notes
        return ResponseEntity.ok(items)
    }

    @GetMapping("/filter")
    fun filter(
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) color: String?,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<CategoryDto>> {
        val filter = CategoryFilterDto(
            title = title,
            color = color
        )
        val categories: List<CategoryDto> = _categoryService.filter(filter)
        return ResponseEntity.ok(categories.toPage(pageable))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @Valid @RequestBody dto: CategoryDto): ResponseEntity<CategoryDto> {
        val updated: CategoryDto = _categoryService.update(id, dto)
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        _eventService.deleteAllByCategoryId(id)
        _taskService.deleteAllCategoryByCategoryId(id)
        _noteService.deleteCategoryByCategoryId(id)
        _categoryService.delete(id)
        return ResponseEntity.noContent().build()
    }

}
