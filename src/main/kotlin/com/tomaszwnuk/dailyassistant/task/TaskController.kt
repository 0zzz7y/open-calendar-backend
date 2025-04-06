package com.tomaszwnuk.dailyassistant.task

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@Suppress("unused")
@RestController
@RequestMapping("/tasks")
class TaskController(private val _taskService: TaskService) {

    @PostMapping
    fun create(@Valid @RequestBody dto: TaskDto): ResponseEntity<TaskDto> {
        val created: TaskDto = _taskService.create(dto).toDto()
        return ResponseEntity.status(201).body(created)
    }

    @GetMapping
    fun getAll(
        @PageableDefault(
            size = 10,
            sort = ["createdAt"],
            direction = Sort.Direction.ASC
        ) pageable: Pageable
    ): ResponseEntity<Page<TaskDto>> {
        val tasksPage: Page<TaskDto> = _taskService.getAll(pageable).map { it.toDto() }
        return ResponseEntity.ok(tasksPage)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<TaskDto> {
        val task: TaskDto = _taskService.getById(id).toDto()
        return ResponseEntity.ok(task)
    }

    @GetMapping("/filter")
    fun filter(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) description: String?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) dateFrom: String?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) dateTo: String?,
        @RequestParam(required = false) recurringPattern: String?,
        @RequestParam(required = false) status: String?,
        @RequestParam(required = false) calendarId: UUID?,
        @RequestParam(required = false) categoryId: UUID?,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.ASC) pageable: Pageable
    ): ResponseEntity<Page<TaskDto>> {
        val filter = TaskFilterDto(
            name = name,
            description = description,
            dateFrom = dateFrom?.let { LocalDateTime.parse(it) },
            dateTo = dateTo?.let { LocalDateTime.parse(it) },
            categoryId = categoryId
        )
        val tasksPage: Page<TaskDto> = _taskService.filter(filter, pageable).map { it.toDto() }
        return ResponseEntity.ok(tasksPage)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @Valid @RequestBody dto: TaskDto): ResponseEntity<TaskDto> {
        val updated: TaskDto = _taskService.update(id, dto).toDto()
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        _taskService.delete(id)
        return ResponseEntity.noContent().build()
    }

}
