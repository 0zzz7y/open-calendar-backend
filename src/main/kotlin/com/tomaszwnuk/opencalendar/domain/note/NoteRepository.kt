/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.note

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

/**
 * Repository interface for managing Note entities.
 * Extends JpaRepository to provide CRUD operations and custom query methods.
 */
interface NoteRepository : JpaRepository<Note, UUID> {

    /**
     * Retrieves all notes associated with a specific calendar.
     *
     * @param calendarId The unique identifier of the calendar.
     *
     * @return A list of notes belonging to the specified calendar.
     */
    fun findAllByCalendarId(calendarId: UUID): List<Note>

    /**
     * Retrieves all notes associated with a specific category.
     *
     * @param categoryId The unique identifier of the category.
     *
     * @return A list of notes belonging to the specified category.
     */
    fun findAllByCategoryId(categoryId: UUID): List<Note>

    /**
     * Filters notes based on the provided criteria.
     *
     * @param title The title to filter by (optional, case-insensitive, partial match).
     * @param description The description to filter by (optional, case-insensitive, partial match).
     * @param calendarId The unique identifier of the calendar to filter by (optional).
     * @param categoryId The unique identifier of the category to filter by (optional).
     *
     * @return A list of notes matching the specified filter criteria.
     */
    @Query(
        """
    SELECT n FROM Note n
    WHERE (:title IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', :title, '%')))
      AND (:description IS NULL OR LOWER(n.description) LIKE LOWER(CONCAT('%', :description, '%')))
      AND (:calendarId IS NULL OR n.calendar.id = :calendarId)
      AND (:categoryId IS NULL OR n.category.id = :categoryId)
    """
    )
    fun filter(
        @Param("title") title: String?,
        @Param("description") description: String?,
        @Param("calendarId") calendarId: UUID?,
        @Param("categoryId") categoryId: UUID?
    ): List<Note>

}
