package com.tomaszwnuk.dailyassistant.category

import com.tomaszwnuk.dailyassistant.domain.info
import com.tomaszwnuk.dailyassistant.domain.validation.assertNameDoesNotExist
import com.tomaszwnuk.dailyassistant.domain.validation.findOrThrow
import org.springframework.stereotype.Service
import java.util.*

@Service
class CategoryService(
    private val _categoryRepository: CategoryRepository
) {

    fun getAll(): List<Category> = _categoryRepository.findAll()

    fun getById(id: UUID): Category {
        info(this, "Fetching category with id $id")
        val category: Category = _categoryRepository.findOrThrow(id)

        info(this, "Found $category")
        return category
    }

    fun create(dto: CategoryDto): Category {
        info(this, "Creating $dto")
        _categoryRepository.assertNameDoesNotExist(
            name = dto.name,
            existsByName = { _categoryRepository.existsByName(it) }
        )

        val category = Category(
            name = dto.name,
            color = dto.color
        )

        info(this, "Created $category")
        return _categoryRepository.save(category)
    }

    fun update(id: UUID, dto: CategoryDto): Category {
        info(this, "Updating $dto")
        val existing: Category = getById(id)
        _categoryRepository.assertNameDoesNotExist(
            name = dto.name,
            existsByName = { _categoryRepository.existsByName(it) }
        )

        val updated: Category = existing.copy(
            name = dto.name,
            color = dto.color
        )

        info(this, "Updated $updated")
        return _categoryRepository.save(updated)
    }

    fun delete(id: UUID) {
        info(this, "Deleting category with id $id.")
        val existing: Category = getById(id)

        info(this, "Deleting category $existing")
        _categoryRepository.delete(existing)
    }

}
