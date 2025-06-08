package com.tomaszwnuk.opencalendar.domain.category

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface CategoryRepository : JpaRepository<Category, UUID> {

    fun existsByName(name: String): Boolean

    @Query(
        """
    SELECT c from Category c
    WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT("%", :name, "%")))
      AND (:color IS NULL OR c.color = :color)
    """
    )
    fun filter(
        @Param("name") name: String?,
        @Param("color") color: String?,
    ): List<Category>

}
