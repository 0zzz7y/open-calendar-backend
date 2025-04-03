package com.tomaszwnuk.dailyassistant.task

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskController(private val _taskService: TaskService) {

    @GetMapping
    fun getAllTasks(): ResponseEntity<List<TaskDto>> {
        val tasks: List<TaskDto> = _taskService.getAll().map { it.toDto() }
        return ResponseEntity.ok(tasks)
    }

    @GetMapping("/{id}")
    fun getTaskById(id: UUID): ResponseEntity<TaskDto> {
        val task: TaskDto = _taskService.getById(id).toDto()
        return ResponseEntity.ok(task)
    }

    @PostMapping
    fun createTask(@RequestBody dto: TaskDto): ResponseEntity<TaskDto> {
        val created: TaskDto = _taskService.create(dto).toDto()
        return ResponseEntity.status(201).body(created)
    }

    @PutMapping("/{id}")
    fun updateTask(@PathVariable id: UUID, @RequestBody dto: TaskDto): ResponseEntity<TaskDto> {
        val updated: TaskDto = _taskService.update(id, dto).toDto()
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun deleteTask(@PathVariable id: UUID): ResponseEntity<Void> {
        _taskService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
