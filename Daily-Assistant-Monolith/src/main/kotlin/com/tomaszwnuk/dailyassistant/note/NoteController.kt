package com.tomaszwnuk.dailyassistant.note

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/notes")
class NoteController(
    private val _noteService: NoteService
) {

    @GetMapping
    fun getNotes(): ResponseEntity<List<NoteDto>> {
        val notes: List<NoteDto> = _noteService.getAll().map { it.toDto() }
        return ResponseEntity.ok(notes)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<NoteDto> {
        val note: NoteDto = _noteService.getById(id).toDto()
        return ResponseEntity.ok(note)
    }

    @PostMapping
    fun create(@Valid @RequestBody dto: NoteDto): ResponseEntity<NoteDto> {
        val created: NoteDto = _noteService.create(dto).toDto()
        return ResponseEntity.status(201).body(created)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @Valid @RequestBody dto: NoteDto): ResponseEntity<NoteDto> {
        val updated: NoteDto = _noteService.update(id, dto).toDto()
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        _noteService.delete(id)
        return ResponseEntity.noContent().build()
    }

}