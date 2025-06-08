package com.tomaszwnuk.opencalendar.domain.calendar

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface CalendarRepository : JpaRepository<Calendar, UUID> {

    fun existsByName(name: String): Boolean

    @Query(
        """
    SELECT c FROM Calendar c
    WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT("%", :name, "%")))
      AND (:emoji IS NULL OR c.emoji = :emoji)
    """,
        nativeQuery = false
    )
    fun filter(
        @Param("name") name: String?,
        @Param("emoji") emoji: String?,
    ): List<Calendar>

}
