package com.tomaszwnuk.dailyassistant.category

import com.tomaszwnuk.dailyassistant.domain.utility.info
import com.tomaszwnuk.dailyassistant.validation.assertNameDoesNotExist
import com.tomaszwnuk.dailyassistant.validation.findOrThrow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class CategoryService(
    private val _categoryRepository: CategoryRepository
) {

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

    fun getAll(): List<Category> {
        info(this, "Fetching all categories")
        val categories: List<Category> = _categoryRepository.findAll()

        info(this, "Found $categories")
        return categories
    }

    fun getAll(pageable: Pageable): Page<Category> {
        info(this, "Fetching all categories")
        val categories: Page<Category> = _categoryRepository.findAll(pageable)

        info(this, "Found $categories")
        return categories
    }

    fun getById(id: UUID): Category {
        info(this, "Fetching category with id $id")
        val category: Category = _categoryRepository.findOrThrow(id)

        info(this, "Found $category")
        return category
    }

    fun filter(filter: CategoryFilterDto, pageable: Pageable): Page<Category> {
        info(this, "Filtering categories with $filter")
        val categories: Page<Category> = _categoryRepository.filter(
            name = filter.name,
            color = filter.color,
            pageable = pageable
        )

        info(this, "Found $categories")
        return categories
    }

    fun update(id: UUID, dto: CategoryDto): Category {
        info(this, "Updating $dto")
        val existing: Category = getById(id)

        val isNameChanged: Boolean = !(dto.name.equals(existing.name, ignoreCase = true))
        if (isNameChanged) {
            _categoryRepository.assertNameDoesNotExist(
                name = dto.name,
                existsByName = { _categoryRepository.existsByName(it) }
            )
        }

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
