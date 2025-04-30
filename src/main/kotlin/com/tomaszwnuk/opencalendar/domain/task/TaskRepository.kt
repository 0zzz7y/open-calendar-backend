/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.task

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

/**
 * Repository interface for managing `Task` entities.
 * Extends the `JpaRepository` to provide CRUD operations and custom query methods for tasks.
 */
interface TaskRepository : JpaRepository<Task, UUID> {

    /**
     * Retrieves all tasks associated with a specific calendar.
     *
     * @param calendarId The unique identifier of the calendar.
     *
     * @return A list of tasks associated with the specified calendar.
     */
    fun findAllByCalendarId(calendarId: UUID): List<Task>

    /**
     * Retrieves all tasks associated with a specific category.
     *
     * @param categoryId The unique identifier of the category.
     *
     * @return A list of tasks associated with the specified category.
     */
    fun findAllByCategoryId(categoryId: UUID): List<Task>

    /**
     * Filters tasks based on the provided criteria.
     *
     * @param title The title of the task to filter by (optional).
     * @param description The description of the task to filter by (optional).
     * @param status The status of the task to filter by (optional).
     * @param calendarId The unique identifier of the associated calendar to filter by (optional).
     * @param categoryId The unique identifier of the associated category to filter by (optional).
     *
     * @return A list of tasks matching the specified filter criteria.
     */
    @Query(
        """
    SELECT t FROM Task t
    WHERE (:title IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%')))
      AND (:description IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :description, '%')))
      AND (:status IS NULL OR t.status = :status)
      AND (:calendarId IS NULL OR t.calendar.id = :calendarId)
      AND (:categoryId IS NULL OR t.category.id = :categoryId)
    """
    )
    fun filter(
        @Param("title") title: String?,
        @Param("description") description: String?,
        @Param("status") status: TaskStatus?,
        @Param("calendarId") calendarId: UUID?,
        @Param("categoryId") categoryId: UUID?
    ): List<Task>

}
