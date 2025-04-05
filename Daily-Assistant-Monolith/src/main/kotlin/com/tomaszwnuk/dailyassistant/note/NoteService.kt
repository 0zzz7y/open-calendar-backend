package com.tomaszwnuk.dailyassistant.note

import com.tomaszwnuk.dailyassistant.category.CategoryRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class NoteService(

    private val _noteRepository: NoteRepository,

    private val _categoryRepository: CategoryRepository,

    ) {

    fun getAll(): List<Note> = _noteRepository.findAll()

    fun getById(id: UUID): Note = _noteRepository.findById(id).orElseThrow {
        NoSuchElementException("Note with id $id could not be found.")
    }

    fun create(dto: NoteDto): Note {
        val category = dto.categoryId?.let {
            _categoryRepository.findById(it).orElseThrow {
                NoSuchElementException("Category with id $it could not be found.")
            }
        }

        val note = Note(
            name = dto.name,
            description = dto.description,
            category = category
        )

        return _noteRepository.save(note)
    }

    fun update(id: UUID, dto: NoteDto): Note {
        val existing = getById(id)
        val category = dto.categoryId?.let {
            _categoryRepository.findById(it).orElseThrow {
                NoSuchElementException("Category with id $it could not be found.")
            }
        }

        val updated = existing.copy(
            name = dto.name,
            description = dto.description,
            category = category
        )

        return _noteRepository.save(updated)
    }

    fun delete(id: UUID) {
        val existing = getById(id)
        _noteRepository.delete(existing)
    }

}