package com.tomaszwnuk.opencalendar.domain.note

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
@RequestMapping("/notes")
class NoteController(
    private val _noteService: NoteService
) {

    @PostMapping
    fun create(@Valid @RequestBody dto: NoteDto): ResponseEntity<NoteDto> {
        val created: NoteDto = _noteService.create(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @GetMapping
    fun getAll(
        @PageableDefault(
            size = 10,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable
    ): ResponseEntity<Page<NoteDto>> {
        val notes: List<NoteDto> = _noteService.getAll()
        return ResponseEntity.ok(notes.toPage(pageable))
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<NoteDto> {
        val note: NoteDto = _noteService.getById(id)
        return ResponseEntity.ok(note)
    }

    @GetMapping("/filter")
    fun filter(
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) description: String?,
        @RequestParam(required = false) calendarId: UUID?,
        @RequestParam(required = false) categoryId: UUID?,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<NoteDto>> {
        val filter = NoteFilterDto(
            title = title,
            description = description,
            calendarId = calendarId,
            categoryId = categoryId
        )
        val notes: List<NoteDto> = _noteService.filter(filter)
        return ResponseEntity.ok(notes.toPage(pageable))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @Valid @RequestBody dto: NoteDto): ResponseEntity<NoteDto> {
        val updated: NoteDto = _noteService.update(id, dto)
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        _noteService.delete(id)
        return ResponseEntity.noContent().build()
    }

}
