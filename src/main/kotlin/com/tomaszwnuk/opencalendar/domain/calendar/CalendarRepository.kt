package com.tomaszwnuk.opencalendar.domain.calendar

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface CalendarRepository : JpaRepository<Calendar, UUID> {

    fun existsByNameAndUserId(name: String, userId: UUID): Boolean

    fun findAllByUserId(userId: UUID): List<Calendar>

    fun findByIdAndUserId(id: UUID, userId: UUID): Optional<Calendar>

    @Query(
        """
    SELECT c FROM Calendar c
    WHERE (c.userId = :userId)
      AND (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT("%", :name, "%")))
      AND (:emoji IS NULL OR c.emoji = :emoji)
    """,
        nativeQuery = false
    )
    fun filter(
        @Param("name") name: String?,
        @Param("emoji") emoji: String?,
        @Param("userId") userId: UUID
    ): List<Calendar>

}
