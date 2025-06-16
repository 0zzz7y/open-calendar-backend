package com.tomaszwnuk.opencalendar.domain.category

import com.tomaszwnuk.opencalendar.domain.user.UserService
import com.tomaszwnuk.opencalendar.utility.logger.info
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import java.util.*

/**
 * The service for category operations.
 */
@Service
class CategoryService(

    /**
     * The repository for managing category data.
     */
    private val _categoryRepository: CategoryRepository,

    /**
     * The service for user operations.
     */
    private val _userService: UserService

) {

    /**
     * The timer for measuring the duration of operations.
     */
    private var _timer: Long = 0

    /**
     * Creates a new category.
     *
     * @param dto The data transfer object containing category details
     *
     * @return The created category as a data transfer object
     *
     * @throws IllegalArgumentException if a category with the same name already exists for the user
     */
    @Caching(
        evict = [
            CacheEvict(cacheNames = ["allCategories"], allEntries = true)
        ]
    )
    fun create(dto: CategoryDto): CategoryDto {
        info(source = this, "Creating $dto")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val existsByName: Boolean = _categoryRepository.existsByNameAndUserId(
            name = dto.name,
            userId = userId
        )
        if (existsByName) {
            throw IllegalArgumentException("Category with name '${dto.name}' already exists for user $userId")
        }

        val category = Category(
            name = dto.name,
            color = dto.color,
            userId = userId
        )

        val created: Category = _categoryRepository.save(category)
        info(source = this, "Created $created in ${System.currentTimeMillis() - _timer} ms")
        return created.toDto()
    }

    /**
     * Retrieves all categories for the current user.
     *
     * @return A list of all categories as data transfer objects
     */
    @Cacheable(cacheNames = ["allCategories"], condition = "#result != null")
    fun getAll(): List<CategoryDto> {
        info(source = this, "Fetching all categories")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val categories: List<Category> = _categoryRepository.findAllByUserId(userId = userId)

        info(source = this, message = "Found $categories in ${System.currentTimeMillis() - _timer} ms")
        return categories.map { it.toDto() }
    }

    /**
     * Retrieves a category by its unique identifier.
     *
     * @param id The unique identifier of the category
     *
     * @return The category as a data transfer object
     *
     * @throws NoSuchElementException if the category with the specified unique identifier does not exist for the user
     */
    @Cacheable(cacheNames = ["categoryById"], key = "#id", condition = "#id != null")
    fun getById(id: UUID): CategoryDto {
        info(source = this, message = "Fetching category with id $id")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val category: Optional<Category> = _categoryRepository.findByIdAndUserId(id = id, userId = userId)
        if (category.isEmpty) {
            throw NoSuchElementException("Category with id $id not found for user $userId")
        }

        info(source = this, message = "Found $category in ${System.currentTimeMillis() - _timer} ms")
        return category.get().toDto()
    }

    /**
     * Filters categories based on the provided criteria.
     *
     * @param filter The filter criteria as a data transfer object
     *
     * @return A list of categories that match the filter criteria
     */
    fun filter(filter: CategoryFilterDto): List<CategoryDto> {
        info(source = this, message = "Filtering categories with $filter")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val categories: List<Category> = _categoryRepository.filter(
            userId = userId,
            name = filter.name,
            color = filter.color
        )

        info(source = this, message = "Found $categories in ${System.currentTimeMillis() - _timer} ms")
        return categories.map { it.toDto() }
    }

    /**
     * Updates an existing category.
     *
     * @param id The unique identifier of the category to update
     * @param dto The data transfer object containing the updated details of the category
     *
     * @return The updated category as a data transfer object
     *
     * @throws NoSuchElementException if the category with the specified unique identifier is not found for the user
     * @throws IllegalArgumentException if a category with the same name already exists for the user
     */
    @Caching(
        evict = [
            CacheEvict(cacheNames = ["categoryById"], key = "#id"),
            CacheEvict(cacheNames = ["allCategories"], allEntries = true)
        ]
    )
    fun update(id: UUID, dto: CategoryDto): CategoryDto {
        info(source = this, message = "Updating $dto")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val existing: Optional<Category> = _categoryRepository.findByIdAndUserId(id = id, userId = userId)
        if (existing.isEmpty) {
            throw NoSuchElementException("Category with id $id not found for user $userId")
        }

        val isNameChanged: Boolean = !(dto.name.equals(other = existing.get().name, ignoreCase = true))
        if (isNameChanged) {
            val existsByName: Boolean = _categoryRepository.existsByNameAndUserId(
                name = dto.name,
                userId = userId
            )
            if (existsByName) {
                throw IllegalArgumentException("Category with name '${dto.name}' already exists for user $userId")
            }
        }

        val changed: Category = existing.get().copy(
            name = dto.name,
            color = dto.color
        )

        val updated: Category = _categoryRepository.save(changed)
        info(source = this, message = "Updated $updated in ${System.currentTimeMillis() - _timer} ms")
        return updated.toDto()
    }

    /**
     * Deletes a category by its unique identifier.
     *
     * @param id The unique identifier of the category to delete
     *
     * @throws NoSuchElementException if the category with the specified unique identifier is not found for the user
     */
    @Caching(
        evict = [
            CacheEvict(cacheNames = ["categoryById"], key = "#id", condition = "#id != null"),
            CacheEvict(cacheNames = ["allCategories"], allEntries = true)
        ]
    )
    fun delete(id: UUID) {
        info(source = this, message = "Deleting category with id $id.")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val existing: Optional<Category> = _categoryRepository.findByIdAndUserId(id = id, userId = userId)
        if (existing.isEmpty) {
            throw NoSuchElementException("Category with id $id not found for user $userId")
        }

        _categoryRepository.delete(existing.get())
        info(source = this, message = "Deleted category $existing in ${System.currentTimeMillis() - _timer} ms")
    }

}
