package com.tomaszwnuk.dailyassistant.category

import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tomaszwnuk.dailyassistant.event.EventDto
import com.tomaszwnuk.dailyassistant.event.EventRepository
import com.tomaszwnuk.dailyassistant.note.NoteDto
import com.tomaszwnuk.dailyassistant.note.NoteRepository
import com.tomaszwnuk.dailyassistant.task.TaskDto
import com.tomaszwnuk.dailyassistant.task.TaskRepository
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/categories")
class CategoryController(
    private val _categoryService: CategoryService,
    private val _eventRepository: EventRepository,
    private val _taskRepository: TaskRepository,
    private val _noteRepository: NoteRepository
) {

    @GetMapping
    fun getAll(): ResponseEntity<List<CategoryDto>> {
        val categories: List<CategoryDto> = _categoryService.getAll().map { it.toDto() }
        return ResponseEntity.ok(categories)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<CategoryDto> {
        val category: CategoryDto = _categoryService.getById(id).toDto()
        return ResponseEntity.ok(category)
    }

    @PostMapping
    fun create(@Valid @RequestBody dto: CategoryDto): ResponseEntity<CategoryDto> {
        val created: CategoryDto = _categoryService.create(dto).toDto()
        return ResponseEntity.status(201).body(created)
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

    @GetMapping("/{id}/items")
    fun getAllItemsForCategory(@PathVariable id: UUID): ResponseEntity<List<Map<String, Any>>> {
        val tasks: List<Map<String, Any>> = _taskRepository.findAllByCategoryId(id).map {
            it.toDto().toMapWithType("task")
        }
        val events: List<Map<String, Any>> = _eventRepository.findAllByCategoryId(id).map {
            it.toDto().toMapWithType("event")
        }
        val notes: List<Map<String, Any>> = _noteRepository.findAllByCategoryId(id).map {
            it.toDto().toMapWithType("note")
        }

        val items: List<Map<String, Any>> = tasks + events + notes
        return ResponseEntity.ok(items)
    }

    @GetMapping("/{id}/events")
    fun getEvents(@PathVariable id: UUID): ResponseEntity<List<EventDto>> {
        val events: List<EventDto> = _eventRepository.findAllByCategoryId(id).map { it.toDto() }
        return ResponseEntity.ok(events)
    }

    @GetMapping("/{id}/tasks")
    fun getTasks(@PathVariable id: UUID): ResponseEntity<List<TaskDto>> {
        val tasks: List<TaskDto> = _taskRepository.findAllByCategoryId(id).map { it.toDto() }
        return ResponseEntity.ok(tasks)
    }

    @GetMapping("/{id}/notes")
    fun getNotes(@PathVariable id: UUID): ResponseEntity<List<NoteDto>> {
        val notes: List<NoteDto> = _noteRepository.findAllByCategoryId(id).map { it.toDto() }
        return ResponseEntity.ok(notes)
    }

    private fun Any.toMapWithType(type: String): Map<String, Any> {
        val map: Map<String, Any> = jacksonObjectMapper().convertValue<Map<String, Any>>(this)
        val mapWithType: Map<String, Any> = map + mapOf("type" to type)
        return mapWithType
    }

}
