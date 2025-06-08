package com.tomaszwnuk.opencalendar.domain.category

import com.tomaszwnuk.opencalendar.utility.logger.info
import com.tomaszwnuk.opencalendar.utility.validation.repository.assertNameDoesNotExist
import com.tomaszwnuk.opencalendar.utility.validation.repository.findOrThrow
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import java.util.*

@Service
class CategoryService(
    private val _categoryRepository: CategoryRepository
) {

    private var _timer: Long = 0

    @Caching(
        evict = [
            CacheEvict(cacheNames = ["allCategories"], allEntries = true)
        ]
    )
    fun create(dto: CategoryDto): CategoryDto {
        info(source = this, "Creating $dto")
        _timer = System.currentTimeMillis()

        _categoryRepository.assertNameDoesNotExist(
            name = dto.name,
            existsByName = { _categoryRepository.existsByName(name = it) }
        )

        val category = Category(
            name = dto.name,
            color = dto.color
        )
        val created: Category = _categoryRepository.save(category)

        info(source = this, "Created $created in ${System.currentTimeMillis() - _timer} ms")
        return created.toDto()
    }

    @Cacheable(cacheNames = ["allCategories"], condition = "#result != null")
    fun getAll(): List<CategoryDto> {
        info(source = this, "Fetching all categories")
        _timer = System.currentTimeMillis()

        val categories: List<Category> = _categoryRepository.findAll()

        info(source = this, message = "Found $categories in ${System.currentTimeMillis() - _timer} ms")
        return categories.map { it.toDto() }
    }

    @Cacheable(cacheNames = ["categoryById"], key = "#id", condition = "#id != null")
    fun getById(id: UUID): CategoryDto {
        info(source = this, message = "Fetching category with id $id")
        _timer = System.currentTimeMillis()

        val category: Category = _categoryRepository.findOrThrow(id = id)

        info(source = this, message = "Found $category in ${System.currentTimeMillis() - _timer} ms")
        return category.toDto()
    }

    fun filter(filter: CategoryFilterDto): List<CategoryDto> {
        info(source = this, message = "Filtering categories with $filter")
        _timer = System.currentTimeMillis()

        val categories: List<Category> = _categoryRepository.filter(
            name = filter.name,
            color = filter.color
        )

        info(source = this, message = "Found $categories in ${System.currentTimeMillis() - _timer} ms")
        return categories.map { it.toDto() }
    }

    @Caching(
        evict = [
            CacheEvict(cacheNames = ["categoryById"], key = "#id"),
            CacheEvict(cacheNames = ["allCategories"], allEntries = true)
        ]
    )
    fun update(id: UUID, dto: CategoryDto): CategoryDto {
        info(source = this, message = "Updating $dto")
        _timer = System.currentTimeMillis()

        val existing: Category = _categoryRepository.findOrThrow(id = id)
        val isNameChanged: Boolean = !(dto.name.equals(other = existing.name, ignoreCase = true))
        if (isNameChanged) {
            _categoryRepository.assertNameDoesNotExist(
                name = dto.name,
                existsByName = { _categoryRepository.existsByName(name = it) }
            )
        }

        val changed: Category = existing.copy(
            name = dto.name,
            color = dto.color
        )
        val updated: Category = _categoryRepository.save(changed)

        info(source = this, message = "Updated $updated in ${System.currentTimeMillis() - _timer} ms")
        return updated.toDto()
    }

    @Caching(
        evict = [
            CacheEvict(cacheNames = ["categoryById"], key = "#id", condition = "#id != null"),
            CacheEvict(cacheNames = ["allCategories"], allEntries = true)
        ]
    )
    fun delete(id: UUID) {
        info(source = this, message = "Deleting category with id $id.")
        _timer = System.currentTimeMillis()

        val existing: Category = _categoryRepository.findOrThrow(id = id)
        _categoryRepository.delete(existing)

        info(source = this, message = "Deleted category $existing in ${System.currentTimeMillis() - _timer} ms")
    }

}
