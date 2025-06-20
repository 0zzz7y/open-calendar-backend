package com.ozzz7y.opencalendar.domain.category

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

/**
 * The repository for managing categories data.
 */
interface CategoryRepository : JpaRepository<Category, UUID> {

    /**
     * Checks if a category with the given name and user ID exists.
     *
     * @param name The name of the category
     * @param userId The unique identifier of the user who owns the category
     *
     * @return true if a category with the given name and user ID exists, false otherwise
     */
    fun existsByNameAndUserId(name: String, userId: UUID): Boolean

    /**
     * Finds all categories that belong to a specific user.
     *
     * @param userId The unique identifier of the user who owns the categories
     *
     * @return A list of categories belonging to the specified user
     */
    fun findAllByUserId(userId: UUID): List<Category>

    /**
     * Finds a category by its unique identifier and the user identifier of the category it belongs to.
     *
     * @param id The unique identifier of the category
     * @param userId The unique identifier of the user who owns the category
     *
     * @return An optional containing the category if found, or empty if not found
     */
    fun findByIdAndUserId(id: UUID, userId: UUID): Optional<Category>

    /**
     * Filters categories based on the provided criteria.
     *
     * @param userId The unique identifier of the user who owns the categories
     * @param name The name of the category (optional)
     * @param color The color of the category (optional)
     *
     * @return A list of categories that match the filter criteria
     */
    @Query(
        """
    SELECT c from Category c
    WHERE (c.userId = :userId)
      AND (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT("%", :name, "%")))
      AND (:color IS NULL OR c.color = :color)
    """
    )
    fun filter(
        @Param("userId") userId: UUID,
        @Param("name") name: String?,
        @Param("color") color: String?
    ): List<Category>

}
