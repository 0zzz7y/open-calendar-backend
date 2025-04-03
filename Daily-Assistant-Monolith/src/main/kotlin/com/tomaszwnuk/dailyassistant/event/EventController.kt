package com.tomaszwnuk.dailyassistant.event

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping
class EventController(
    private val _eventService: EventService
) {

    @GetMapping
    fun getEvents(): ResponseEntity<List<EventDto>> {
        val events: List<EventDto> = _eventService.getAll().map { it.toDto() }
        return ResponseEntity.ok(events)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<EventDto> {
        val event: EventDto = _eventService.getById(id).toDto()
        return ResponseEntity.ok(event)
    }

    @PostMapping
    fun create(@RequestBody dto: EventDto): ResponseEntity<EventDto> {
        val created: EventDto = _eventService.create(dto).toDto()
        return ResponseEntity.status(201).body(created)
    }

    @PutMapping
    fun update(@PathVariable id: UUID, @RequestBody dto: EventDto): ResponseEntity<EventDto> {
        val updated: EventDto = _eventService.update(id, dto).toDto()
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        _eventService.delete(id)
        return ResponseEntity.noContent().build()
    }
}