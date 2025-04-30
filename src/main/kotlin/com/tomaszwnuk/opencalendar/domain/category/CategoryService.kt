/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.category

import com.tomaszwnuk.opencalendar.utility.logger.info
import com.tomaszwnuk.opencalendar.utility.validation.assertNameDoesNotExist
import com.tomaszwnuk.opencalendar.utility.validation.findOrThrow
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import java.util.*

/**
 * Service class for managing categories.
 * Provides methods for creating, retrieving, updating, and deleting categories,
 * as well as filtering categories based on specific criteria.
 *
 * @property _categoryRepository The repository for accessing and managing `Category` entities.
 */
@Service
class CategoryService(
    private val _categoryRepository: CategoryRepository
) {

    /**
     * Timer used for logging execution time of operations.
     */
    private var _timer: Long = 0

    /**
     * Creates a new category.
     * Evicts the cache for all categories after creation.
     *
     * @param dto The data transfer object containing category details.
     *
     * @return The created category as a DTO.
     */
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

    /**
     * Retrieves all categories.
     * Caches the result to improve performance.
     *
     * @return A list of all categories as DTOs.
     */
    @Cacheable(cacheNames = ["allCategories"], condition = "#result != null")
    fun getAll(): List<CategoryDto> {
        info(this, "Fetching all categories")
        _timer = System.currentTimeMillis()

        val categories: List<Category> = _categoryRepository.findAll()

        info(this, "Found $categories in ${System.currentTimeMillis() - _timer} ms")
        return categories.map { it.toDto() }
    }

    /**
     * Retrieves a category by its ID.
     * Caches the result to improve performance.
     *
     * @param id The unique identifier of the category.
     *
     * @return The category as a DTO.
     */
    @Cacheable(cacheNames = ["categoryById"], key = "#id", condition = "#id != null")
    fun getById(id: UUID): CategoryDto {
        info(this, "Fetching category with id $id")
        _timer = System.currentTimeMillis()

        val category: Category = _categoryRepository.findOrThrow(id)

        info(this, "Found $category in ${System.currentTimeMillis() - _timer} ms")
        return category.toDto()
    }

    /**
     * Filters categories based on the provided criteria.
     *
     * @param filter The filter criteria containing title and/or color.
     *
     * @return A list of categories matching the filter as DTOs.
     */
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

    /**
     * Updates an existing category.
     * Evicts the cache for the specific category and all categories after the update.
     *
     * @param id The unique identifier of the category to update.
     * @param dto The data transfer object containing updated category details.
     *
     * @return The updated category as a DTO.
     */
    @Caching(
        evict = [
            CacheEvict(cacheNames = ["categoryById"], key = "#id"),
            CacheEvict(cacheNames = ["allCategories"], allEntries = true)
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

    /**
     * Deletes a category by its ID.
     * Evicts the cache for the specific category and all categories after deletion.
     *
     * @param id The unique identifier of the category to delete.
     */
    @Caching(
        evict = [
            CacheEvict(cacheNames = ["categoryById"], key = "#id", condition = "#id != null"),
            CacheEvict(cacheNames = ["allCategories"], allEntries = true)
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
