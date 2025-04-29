package com.tomaszwnuk.opencalendar.domain.task

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface TaskRepository : JpaRepository<Task, UUID> {

    fun findAllByCalendarId(calendarId: UUID): List<Task>

    fun findAllByCategoryId(categoryId: UUID): List<Task>

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
