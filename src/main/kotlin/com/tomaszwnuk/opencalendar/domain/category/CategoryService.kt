package com.tomaszwnuk.opencalendar.domain.category

import com.tomaszwnuk.opencalendar.utility.logger.info
import com.tomaszwnuk.opencalendar.utility.validation.assertNameDoesNotExist
import com.tomaszwnuk.opencalendar.utility.validation.findOrThrow
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
        info(this, "Creating $dto")
        _timer = System.currentTimeMillis()

        _categoryRepository.assertNameDoesNotExist(
            name = dto.title,
            existsByName = { _categoryRepository.existsByTitle(it) }
        )

        val category = Category(
            title = dto.title,
            color = dto.color
        )
        val created: Category = _categoryRepository.save(category)

        info(this, "Created $created in ${System.currentTimeMillis() - _timer} ms")
        return created.toDto()
    }

    @Cacheable(cacheNames = ["allCategories"])
    fun getAll(): List<CategoryDto> {
        info(this, "Fetching all categories")
        _timer = System.currentTimeMillis()

        val categories: List<Category> = _categoryRepository.findAll()

        info(this, "Found $categories in ${System.currentTimeMillis() - _timer} ms")
        return categories.map { it.toDto() }
    }

    @Cacheable(cacheNames = ["categoryById"], key = "#id")
    fun getById(id: UUID): CategoryDto {
        info(this, "Fetching category with id $id")
        _timer = System.currentTimeMillis()

        val category: Category = _categoryRepository.findOrThrow(id)

        info(this, "Found $category in ${System.currentTimeMillis() - _timer} ms")
        return category.toDto()
    }

    fun filter(filter: CategoryFilterDto): List<CategoryDto> {
        info(this, "Filtering categories with $filter")
        _timer = System.currentTimeMillis()

        val categories: List<Category> = _categoryRepository.filter(
            title = filter.title,
            color = filter.color
        )

        info(this, "Found $categories in ${System.currentTimeMillis() - _timer} ms")
        return categories.map { it.toDto() }
    }

    @Caching(
        evict = [
            CacheEvict(cacheNames = ["allCategories"], allEntries = true),
            CacheEvict(cacheNames = ["categoryById"], key = "#id")
        ]
    )
    fun update(id: UUID, dto: CategoryDto): CategoryDto {
        info(this, "Updating $dto")
        _timer = System.currentTimeMillis()

        val existing: Category = _categoryRepository.findOrThrow(id = id)
        val isNameChanged: Boolean = !(dto.title.equals(existing.title, ignoreCase = true))
        if (isNameChanged) {
            _categoryRepository.assertNameDoesNotExist(
                name = dto.title,
                existsByName = { _categoryRepository.existsByTitle(it) }
            )
        }

        val changed: Category = existing.copy(
            title = dto.title,
            color = dto.color
        )
        val updated: Category = _categoryRepository.save(changed)

        info(this, "Updated $updated in ${System.currentTimeMillis() - _timer} ms")
        return updated.toDto()
    }

    @Caching(
        evict = [
            CacheEvict(cacheNames = ["allCategories"], allEntries = true),
            CacheEvict(cacheNames = ["categoryById"], key = "#id")
        ]
    )
    fun delete(id: UUID) {
        info(this, "Deleting category with id $id.")
        _timer = System.currentTimeMillis()

        val existing: Category = _categoryRepository.findOrThrow(id = id)
        _categoryRepository.delete(existing)

        info(this, "Deleted category $existing in ${System.currentTimeMillis() - _timer} ms")
    }

}
