/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.event

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.*

/**
 * Repository interface for managing Event entities.
 * Extends JpaRepository to provide basic CRUD operations and custom query methods.
 */
interface EventRepository : JpaRepository<Event, UUID> {

    /**
     * Retrieves all events associated with a specific calendar.
     *
     * @param calendarId The unique identifier of the calendar.
     * @return A list of events belonging to the specified calendar.
     */
    fun findAllByCalendarId(calendarId: UUID): List<Event>

    /**
     * Retrieves all events associated with a specific category.
     *
     * @param categoryId The unique identifier of the category.
     * @return A list of events belonging to the specified category.
     */
    fun findAllByCategoryId(categoryId: UUID): List<Event>

    /**
     * Filters events based on various optional criteria.
     *
     * @param title The title of the event to filter by (optional).
     * @param description The description of the event to filter by (optional).
     * @param dateFrom The start date for filtering events (optional).
     * @param dateTo The end date for filtering events (optional).
     * @param recurringPattern The recurring pattern of the event to filter by (optional).
     * @param calendarId The ID of the calendar to filter events by (optional).
     * @param categoryId The ID of the category to filter events by (optional).
     * @return A list of events matching the specified criteria.
     */
    @Query(
        """
    SELECT e FROM Event e
    WHERE (:title IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%')))
      AND (:description IS NULL OR LOWER(e.description) LIKE LOWER(CONCAT('%', :description, '%')))
      AND (:dateFrom IS NULL OR e.startDate >= :dateFrom)
      AND (:dateTo IS NULL OR e.endDate <= :dateTo)
      AND (:recurringPattern IS NULL OR e.recurringPattern = :recurringPattern)
      AND (:calendarId IS NULL OR e.calendar.id = :calendarId)
      AND (:categoryId IS NULL OR e.category.id = :categoryId)
    """
    )
    fun filter(
        @Param("title") title: String?,
        @Param("description") description: String?,
        @Param("dateFrom") dateFrom: LocalDateTime?,
        @Param("dateTo") dateTo: LocalDateTime?,
        @Param("recurringPattern") recurringPattern: RecurringPattern?,
        @Param("calendarId") calendarId: UUID?,
        @Param("categoryId") categoryId: UUID?
    ): List<Event>

}
