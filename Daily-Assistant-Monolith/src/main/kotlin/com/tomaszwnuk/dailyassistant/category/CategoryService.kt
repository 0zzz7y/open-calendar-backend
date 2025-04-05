package com.tomaszwnuk.dailyassistant.category

import org.springframework.stereotype.Service
import java.util.*

@Service
class CategoryService(

    private val _categoryRepository: CategoryRepository

) {

    fun getAll(): List<Category> = _categoryRepository.findAll()

    fun getById(id: UUID): Category = _categoryRepository.findById(id).orElseThrow {
        NoSuchElementException("Category with id $id could not be found.")
    }

    fun create(dto: CategoryDto): Category {
        if (_categoryRepository.existsByName(dto.name)) {
            throw IllegalArgumentException("Category with name ${dto.name} already exists.")
        }

        val category = Category(
            name = dto.name,
            color = dto.color
        )

        return _categoryRepository.save(category)
    }

    fun update(id: UUID, dto: CategoryDto): Category {
        val existing = getById(id)

        val isNameValid = _categoryRepository.existsByName(dto.name) && existing.name != dto.name
        if (!isNameValid) {
            throw IllegalArgumentException("Category with name ${dto.name} already exists.")
        }

        val updated = existing.copy(
            name = dto.name,
            color = dto.color
        )

        return _categoryRepository.save(updated)
    }

    fun delete(id: UUID) {
        val existing = getById(id)
        _categoryRepository.delete(existing)
    }

}
