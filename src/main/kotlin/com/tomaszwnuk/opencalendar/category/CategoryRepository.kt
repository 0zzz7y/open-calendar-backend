package com.tomaszwnuk.opencalendar.category

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface CategoryRepository : JpaRepository<Category, UUID> {

    fun existsByTitle(title: String): Boolean

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
        pageable: Pageable
    ): Page<Category>

}
