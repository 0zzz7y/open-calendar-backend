/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.calendar

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

/**
 * Repository interface for managing `Calendar` entities.
 * Extends the `JpaRepository` to provide CRUD operations and custom queries.
 */
interface CalendarRepository : JpaRepository<Calendar, UUID> {

    /**
     * Checks if a calendar with the given title exists.
     *
     * @param title The title of the calendar to check.
     *
     * @return `true` if a calendar with the given title exists, `false` otherwise.
     */
    fun existsByTitle(title: String): Boolean

    /**
     * Filters calendars based on the provided title and emoji.
     * If a parameter is `null`, it is ignored in the filtering criteria.
     *
     * @param title The title to filter by (optional, case-insensitive, partial match).
     * @param emoji The emoji to filter by (optional, exact match).
     *
     * @return A list of calendars matching the filtering criteria.
     */
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
    ): List<Calendar>

}
