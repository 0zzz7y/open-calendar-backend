package com.tomaszwnuk.opencalendar.category

import com.tomaszwnuk.opencalendar.utility.ItemTypeMapper.toMapWithType
import com.tomaszwnuk.opencalendar.event.EventDto
import com.tomaszwnuk.opencalendar.event.EventService
import com.tomaszwnuk.opencalendar.note.NoteDto
import com.tomaszwnuk.opencalendar.note.NoteService
import com.tomaszwnuk.opencalendar.task.TaskDto
import com.tomaszwnuk.opencalendar.task.TaskService
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
        val created: CategoryDto = _categoryService.create(dto).toDto()
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @GetMapping
    fun getAll(
        @PageableDefault(
            size = 10,
            sort = ["name"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable
    ): ResponseEntity<Page<CategoryDto>> {
        val categories: Page<CategoryDto> = _categoryService.getAll(pageable).map { it.toDto() }
        return ResponseEntity.ok(categories)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<CategoryDto> {
        val category: CategoryDto = _categoryService.getById(id).toDto()
        return ResponseEntity.ok(category)
    }

    @GetMapping("/{id}/events")
    fun getEvents(
        @PathVariable id: UUID,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<EventDto>> {
        val events: Page<EventDto> = _eventService.getAllByCategoryId(id, pageable).map { it.toDto() }
        return ResponseEntity.ok(events)
    }

    @GetMapping("/{id}/tasks")
    fun getTasks(
        @PathVariable id: UUID,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<TaskDto>> {
        val tasks: Page<TaskDto> = _taskService.getAllByCategoryId(id, pageable).map { it.toDto() }
        return ResponseEntity.ok(tasks)
    }

    @GetMapping("/{id}/notes")
    fun getNotes(
        @PathVariable id: UUID,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<NoteDto>> {
        val notes: Page<NoteDto> = _noteService.getAllByCategoryId(id, pageable).map { it.toDto() }
        return ResponseEntity.ok(notes)
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
        val events: Page<Map<String, Any>> = _eventService.getAllByCategoryId(id, pageable).map {
            it.toDto().toMapWithType("event")
        }
        val tasks: Page<Map<String, Any>> = _taskService.getAllByCategoryId(id, pageable).map {
            it.toDto().toMapWithType("task")
        }
        val notes: Page<Map<String, Any>> = _noteService.getAllByCategoryId(id, pageable).map {
            it.toDto().toMapWithType("note")
        }

        val items: List<Map<String, Any>> = events + tasks + notes
        return ResponseEntity.ok(items)
    }

    @GetMapping("/filter")
    fun filter(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) color: String?,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<CategoryDto>> {
        val filter = CategoryFilterDto(
            title = name,
            color = color
        )
        val categories: Page<CategoryDto> = _categoryService.filter(filter, pageable).map { it.toDto() }
        return ResponseEntity.ok(categories)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @Valid @RequestBody dto: CategoryDto): ResponseEntity<CategoryDto> {
        val updated: CategoryDto = _categoryService.update(id, dto).toDto()
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        _eventService.deleteAllCategoryByCategoryId(id)
        _taskService.deleteAllCategoryByCategoryId(id)
        _noteService.deleteAllCategoryByCategoryId(id)
        _categoryService.delete(id)
        return ResponseEntity.noContent().build()
    }

}
