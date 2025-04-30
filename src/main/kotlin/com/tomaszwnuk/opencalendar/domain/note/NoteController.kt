/*
 * Copyright (c) Tomasz Wnuk
 */

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

/**
 * REST controller for managing notes.
 * Provides endpoints for creating, retrieving, updating, and deleting notes.
 *
 * @property _noteService The service handling note-related operations.
 */
@Suppress("unused")
@RestController
@RequestMapping("/notes")
class NoteController(
    private val _noteService: NoteService
) {

    /**
     * Creates a new note.
     *
     * @param dto The data transfer object containing note details.
     *
     * @return A ResponseEntity containing the created note as a DTO.
     */
    @PostMapping
    fun create(@Valid @RequestBody(required = true) dto: NoteDto): ResponseEntity<NoteDto> {
        val created: NoteDto = _noteService.create(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    /**
     * Retrieves all notes with pagination.
     *
     * @param pageable The pagination and sorting information.
     *
     * @return A ResponseEntity containing a paginated list of notes as DTOs.
     */
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

    /**
     * Retrieves a note by its unique identifier.
     *
     * @param id The UUID of the note to retrieve.
     *
     * @return A ResponseEntity containing the note as a DTO.
     */
    @GetMapping("/{id}")
    fun getById(@PathVariable(name = "id", required = true) id: UUID): ResponseEntity<NoteDto> {
        val note: NoteDto = _noteService.getById(id)
        return ResponseEntity.ok(note)
    }

    /**
     * Filters notes based on the provided criteria.
     *
     * @param title The title to filter by (optional).
     * @param description The description to filter by (optional).
     * @param calendarId The calendar ID to filter by (optional).
     * @param categoryId The category ID to filter by (optional).
     * @param pageable The pagination and sorting information.
     *
     * @return A ResponseEntity containing a paginated list of filtered notes as DTOs.
     */
    @GetMapping("/filter")
    fun filter(
        @RequestParam(name = "title", required = false) title: String?,
        @RequestParam(name = "description", required = false) description: String?,
        @RequestParam(name = "calendarId", required = false) calendarId: UUID?,
        @RequestParam(name = "categoryId", required = false) categoryId: UUID?,
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

    /**
     * Updates an existing note.
     *
     * @param id The UUID of the note to update.
     * @param dto The data transfer object containing updated note details.
     *
     * @return A ResponseEntity containing the updated note as a DTO.
     */
    @PutMapping("/{id}")
    fun update(
        @PathVariable(name = "id", required = true) id: UUID,
        @Valid @RequestBody(required = true) dto: NoteDto
    ): ResponseEntity<NoteDto> {
        val updated: NoteDto = _noteService.update(id, dto)
        return ResponseEntity.ok(updated)
    }

    /**
     * Deletes a note by its unique identifier.
     *
     * @param id The UUID of the note to delete.
     *
     * @return A ResponseEntity with no content.
     */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable(name = "id", required = true) id: UUID): ResponseEntity<Void> {
        _noteService.delete(id)
        return ResponseEntity.noContent().build()
    }

}
