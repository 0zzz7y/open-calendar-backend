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

@Suppress("unused")
@RestController
@RequestMapping("/tasks")
class TaskController(private val _taskService: TaskService) {

    @PostMapping
    fun create(@Valid @RequestBody dto: TaskDto): ResponseEntity<TaskDto> {
        val created: TaskDto = _taskService.create(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

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

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<TaskDto> {
        val task: TaskDto = _taskService.getById(id)
        return ResponseEntity.ok(task)
    }

    @GetMapping("/filter")
    fun filter(
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) description: String?,
        @RequestParam(required = false) status: String?,
        @RequestParam(required = false) calendarId: UUID?,
        @RequestParam(required = false) categoryId: UUID?,
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

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @Valid @RequestBody dto: TaskDto): ResponseEntity<TaskDto> {
        val updated: TaskDto = _taskService.update(id, dto)
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        _taskService.delete(id)
        return ResponseEntity.noContent().build()
    }

}
