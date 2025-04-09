package com.tomaszwnuk.dailyassistant.category

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tomaszwnuk.dailyassistant.event.EventDto
import com.tomaszwnuk.dailyassistant.event.EventRepository
import com.tomaszwnuk.dailyassistant.note.NoteDto
import com.tomaszwnuk.dailyassistant.note.NoteRepository
import com.tomaszwnuk.dailyassistant.task.TaskDto
import com.tomaszwnuk.dailyassistant.task.TaskRepository
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@Suppress("unused")
@RestController
@RequestMapping("/categories")
class CategoryController(
    private val _categoryService: CategoryService,
    private val _eventRepository: EventRepository,
    private val _taskRepository: TaskRepository,
    private val _noteRepository: NoteRepository
) {

    @PostMapping
    fun create(@Valid @RequestBody dto: CategoryDto): ResponseEntity<CategoryDto> {
        val created: CategoryDto = _categoryService.create(dto).toDto()
        return ResponseEntity.status(201).body(created)
    }

    @GetMapping
    fun getAll(
        @PageableDefault(
            size = 10,
            sort = ["name"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable
    ): ResponseEntity<Page<CategoryDto>> {
        val categoriesPage: Page<CategoryDto> = _categoryService.getAll(pageable).map { it.toDto() }
        return ResponseEntity.ok(categoriesPage)
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
        val eventsPage: Page<EventDto> = _eventRepository.findAllByCategoryId(id, pageable).map { it.toDto() }
        return ResponseEntity.ok(eventsPage)
    }

    @GetMapping("/{id}/tasks")
    fun getTasks(
        @PathVariable id: UUID,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<TaskDto>> {
        val tasksPage: Page<TaskDto> = _taskRepository.findAllByCategoryId(id, pageable).map { it.toDto() }
        return ResponseEntity.ok(tasksPage)
    }

    @GetMapping("/{id}/notes")
    fun getNotes(
        @PathVariable id: UUID,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<NoteDto>> {
        val notesPage: Page<NoteDto> = _noteRepository.findAllByCategoryId(id, pageable).map { it.toDto() }
        return ResponseEntity.ok(notesPage)
    }

    @GetMapping("/{id}/items")
    fun getAllItems(
        @PathVariable id: UUID, @PageableDefault(
            size = 10,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable
    ): ResponseEntity<List<Map<String, Any>>> {
        val events: Page<Map<String, Any>> = _eventRepository.findAllByCategoryId(id, pageable).map {
            it.toDto().toMapWithType("event")
        }
        val tasks: Page<Map<String, Any>> = _taskRepository.findAllByCategoryId(id, pageable).map {
            it.toDto().toMapWithType("task")
        }
        val notes: Page<Map<String, Any>> = _noteRepository.findAllByCategoryId(id, pageable).map {
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
            name = name,
            color = color
        )
        val result: Page<CategoryDto> = _categoryService.filter(filter, pageable).map { it.toDto() }
        return ResponseEntity.ok(result)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @Valid @RequestBody dto: CategoryDto): ResponseEntity<CategoryDto> {
        val updated: CategoryDto = _categoryService.update(id, dto).toDto()
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        _categoryService.delete(id)
        return ResponseEntity.noContent().build()
    }

    private fun Any.toMapWithType(type: String): Map<String, Any> {
        val map: Map<String, Any> = jacksonObjectMapper().registerModule(JavaTimeModule()).convertValue<Map<String, Any>>(this)
        val mapWithType: Map<String, Any> = map + mapOf("type" to type)
        return mapWithType
    }

}
