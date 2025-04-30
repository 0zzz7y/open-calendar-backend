/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.category

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

/**
 * Repository interface for managing `Category` entities.
 * Extends the `JpaRepository` to provide CRUD operations and custom queries.
 */
interface CategoryRepository : JpaRepository<Category, UUID> {

    /**
     * Checks if a category with the given title exists.
     *
     * @param title The title of the category to check.
     *
     * @return `true` if a category with the given title exists, `false` otherwise.
     */
    fun existsByTitle(title: String): Boolean

    /**
     * Filters categories based on the provided title and color.
     * If a parameter is null, it is ignored in the filtering criteria.
     *
     * @param title The title to filter categories by (optional).
     * @param color The color to filter categories by (optional).
     *
     * @return A list of categories matching the filter criteria.
     */
    @Query(
        """
    SELECT c from Category c
    WHERE (:title IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT("%", :title, "%")))
      AND (:color IS NULL OR c.color = :color)
    """
    )
    fun filter(
        @Param("title") title: String?,
        @Param("color") color: String?,
    ): List<Category>

}
