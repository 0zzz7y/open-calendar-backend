package com.tomaszwnuk.opencalendar.category

import com.tomaszwnuk.opencalendar.utility.info
import com.tomaszwnuk.opencalendar.validation.assertNameDoesNotExist
import com.tomaszwnuk.opencalendar.validation.findOrThrow
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class CategoryService(
    private val _categoryRepository: CategoryRepository
) {

    private var _timer: Long = 0

    @Caching(evict = [
        CacheEvict(cacheNames = ["allCategories"], allEntries = true)
    ])
    fun create(dto: CategoryDto): Category {
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

        return created
    }

    @Cacheable(cacheNames = ["allCategories"])
    fun getAll(pageable: Pageable): Page<Category> {
        info(this, "Fetching all categories")
        _timer = System.currentTimeMillis()
        val categories: Page<Category> = _categoryRepository.findAll(pageable)

        info(this, "Found $categories in ${System.currentTimeMillis() - _timer} ms")
        return categories
    }

    @Cacheable(cacheNames = ["categoryById"], key = "#id")
    fun getById(id: UUID): Category {
        info(this, "Fetching category with id $id")
        _timer = System.currentTimeMillis()
        val category: Category = _categoryRepository.findOrThrow(id)

        info(this, "Found $category in ${System.currentTimeMillis() - _timer} ms")
        return category
    }

    fun filter(filter: CategoryFilterDto, pageable: Pageable): Page<Category> {
        info(this, "Filtering categories with $filter")
        _timer = System.currentTimeMillis()
        val categories: Page<Category> = _categoryRepository.filter(
            title = filter.title,
            color = filter.color,
            pageable = pageable
        )

        info(this, "Found $categories in ${System.currentTimeMillis() - _timer} ms")
        return categories
    }

    @Caching(evict = [
        CacheEvict(cacheNames = ["allCategories"], allEntries = true),
        CacheEvict(cacheNames = ["categoryById"], key = "#id")
    ])
    fun update(id: UUID, dto: CategoryDto): Category {
        info(this, "Updating $dto")
        _timer = System.currentTimeMillis()
        val existing: Category = getById(id)

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

        return updated
    }

    @Caching(evict = [
        CacheEvict(cacheNames = ["allCategories"], allEntries = true),
        CacheEvict(cacheNames = ["categoryById"], key = "#id")
    ])
    fun delete(id: UUID) {
        info(this, "Deleting category with id $id.")
        _timer = System.currentTimeMillis()
        val existing: Category = getById(id)

        _categoryRepository.delete(existing)
        info(this, "Deleted category $existing in ${System.currentTimeMillis() - _timer} ms")
    }

}
