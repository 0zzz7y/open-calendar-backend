package com.tomaszwnuk.opencalendar.domain.task

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface TaskRepository : JpaRepository<Task, UUID> {

    @Query(
        """
    SELECT t FROM Task t
    WHERE (t.id = :id)
      AND (t.calendar.userId = :userId)
    """
    )
    fun findByIdAndCalendarUserId(@Param("id") id: UUID, @Param("userId") userId: UUID): Optional<Task>

    @Query(
        """
    SELECT t FROM Task t
    WHERE (t.calendar.userId = :userId)
    """
    )
    fun findAllByCalendarUserId(@Param("userId") userId: UUID): List<Task>

    @Query(
        """
    SELECT t FROM Task t
    WHERE (t.calendar.id = :calendarId)
      AND (t.calendar.userId = :userId)
    """
    )
    fun findAllByCalendarIdAndCalendarUserId(@Param("calendarId") calendarId: UUID, @Param("userId") userId: UUID): List<Task>

    @Query(
        """
    SELECT t FROM Task t
    WHERE (t.category.id = :categoryId)
      AND (t.calendar.userId = :userId)
    """
    )
    fun findAllByCategoryIdAndCalendarUserId(@Param("categoryId") categoryId: UUID, @Param("userId") userId: UUID): List<Task>

    @Query(
        """
    SELECT t FROM Task t
    WHERE (t.calendar.userId = :userId)
      AND (:name IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%')))
      AND (:description IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :description, '%')))
      AND (:status IS NULL OR t.status = :status)
      AND (:calendarId IS NULL OR t.calendar.id = :calendarId)
      AND (:categoryId IS NULL OR t.category.id = :categoryId)
    """
    )
    fun filter(
        @Param("userId") userId: UUID,
        @Param("name") name: String?,
        @Param("description") description: String?,
        @Param("status") status: TaskStatus?,
        @Param("calendarId") calendarId: UUID?,
        @Param("categoryId") categoryId: UUID?
    ): List<Task>

}
