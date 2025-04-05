package com.tomaszwnuk.dailyassistant.note

import com.tomaszwnuk.dailyassistant.category.Category
import com.tomaszwnuk.dailyassistant.category.CategoryRepository
import com.tomaszwnuk.dailyassistant.domain.info
import com.tomaszwnuk.dailyassistant.domain.validation.findOrThrow
import org.springframework.stereotype.Service
import java.util.*

@Service
class NoteService(
    private val _noteRepository: NoteRepository,
    private val _categoryRepository: CategoryRepository,
) {

    fun getAll(): List<Note> {
        info(this, "Fetching all notes")
        val notes: List<Note> = _noteRepository.findAll()

        info(this, "Found $notes")
        return notes
    }

    fun getById(id: UUID): Note {
        info(this, "Fetching note with id $id")
        val note: Note = _noteRepository.findOrThrow(id)

        info(this, "Found $note")
        return note
    }

    fun create(dto: NoteDto): Note {
        info(this, "Creating $dto")
        val category: Category? = dto.categoryId?.let { _categoryRepository.findOrThrow(id = it) }

        val note = Note(
            name = dto.name, description = dto.description, category = category
        )

        info(this, "Created $note")
        return _noteRepository.save(note)
    }

    fun update(id: UUID, dto: NoteDto): Note {
        info(this, "Updating $dto")
        val existing: Note = getById(id)
        val category: Category? = dto.categoryId?.let { _categoryRepository.findOrThrow(id = it) }

        val updated: Note = existing.copy(
            name = dto.name, description = dto.description, category = category
        )

        info(this, "Updated $updated")
        return _noteRepository.save(updated)
    }

    fun delete(id: UUID) {
        info(this, "Deleting note with id $id.")
        val existing: Note = getById(id)

        info(this, "Deleting note $existing")
        _noteRepository.delete(existing)
    }

}