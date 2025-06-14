package com.tomaszwnuk.opencalendar.domain.note

import com.tomaszwnuk.opencalendar.domain.communication.CommunicationConstants
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
 * The controller for managing notes.
 */
@Suppress("unused")
@RestController
@RequestMapping("/${CommunicationConstants.API}/${CommunicationConstants.API_VERSION}/notes")
class NoteController(

    /**
     * The service for performing operations on notes.
     */
    private val _noteService: NoteService

) {

    /**
     * Creates a new note.
     *
     * @param dto The data transfer object containing the details of the note
     *
     * @return A response containing the created note
     */
    @PostMapping
    fun create(@Valid @RequestBody(required = true) dto: NoteDto): ResponseEntity<NoteDto> {
        val created: NoteDto = _noteService.create(dto = dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    /**
     * Retrieves all notes.
     *
     * @param pageable The pagination information
     *
     * @return A response containing a page of notes
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
        return ResponseEntity.ok(notes.toPage(pageable = pageable))
    }

    /**
     * Retrieves a note by its unique identifier.
     *
     * @param id The unique identifier of the note
     *
     * @return A response containing the note with the specified identifier
     */
    @GetMapping("/{id}")
    fun getById(@PathVariable(name = "id", required = true) id: UUID): ResponseEntity<NoteDto> {
        val note: NoteDto = _noteService.getById(id = id)
        return ResponseEntity.ok(note)
    }

    /**
     * Retrieves filtered notes based on the provided criteria.
     *
     * @param name The name of the note (optional)
     * @param description The description of the note (optional)
     * @param calendarId The unique identifier of the calendar associated with the note (optional)
     * @param categoryId The unique identifier of the category associated with the note (optional)
     * @param pageable The pagination information
     *
     * @return A response containing a page of notes that match the filter criteria
     */
    @GetMapping("/filter")
    fun filter(
        @RequestParam(name = "name", required = false) name: String?,
        @RequestParam(name = "description", required = false) description: String?,
        @RequestParam(name = "calendarId", required = false) calendarId: UUID?,
        @RequestParam(name = "categoryId", required = false) categoryId: UUID?,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<NoteDto>> {
        val filter = NoteFilterDto(
            name = name,
            description = description,
            calendarId = calendarId,
            categoryId = categoryId
        )
        val notes: List<NoteDto> = _noteService.filter(filter = filter)
        return ResponseEntity.ok(notes.toPage(pageable = pageable))
    }

    /**
     * Updates an existing note.
     *
     * @param id The unique identifier of the note to update
     * @param dto The data transfer object containing the updated details of the note
     *
     * @return A response containing the updated note
     */
    @PutMapping("/{id}")
    fun update(
        @PathVariable(name = "id", required = true) id: UUID,
        @Valid @RequestBody(required = true) dto: NoteDto
    ): ResponseEntity<NoteDto> {
        val updated: NoteDto = _noteService.update(id = id, dto = dto)
        return ResponseEntity.ok(updated)
    }

    /**
     * Deletes a note by its unique identifier.
     *
     * @param id The unique identifier of the note to delete
     *
     * @return A response indicating the deletion status
     */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable(name = "id", required = true) id: UUID): ResponseEntity<Void> {
        _noteService.delete(id = id)
        return ResponseEntity.noContent().build()
    }

}
