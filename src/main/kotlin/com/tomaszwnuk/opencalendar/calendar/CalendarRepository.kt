package com.tomaszwnuk.opencalendar.calendar

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface CalendarRepository : JpaRepository<Calendar, UUID> {

    fun existsByTitle(title: String): Boolean

    @Query(
        """
    SELECT c FROM Calendar c
    WHERE (:title IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT("%", :title, "%")))
      AND (:emoji IS NULL OR c.emoji = :emoji)
    """,
        nativeQuery = false
    )
    fun filter(
        @Param("title") title: String?,
        @Param("emoji") emoji: String?,
        pageable: Pageable
    ): Page<Calendar>

}
