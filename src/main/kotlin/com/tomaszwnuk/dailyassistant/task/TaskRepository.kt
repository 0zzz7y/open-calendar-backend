package com.tomaszwnuk.dailyassistant.task

import com.tomaszwnuk.dailyassistant.domain.RecurringPattern
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.*

interface TaskRepository : JpaRepository<Task, UUID> {

    fun findAllByCalendarId(calendarId: UUID, pageable: Pageable): Page<Task>

    fun findAllByCategoryId(categoryId: UUID, pageable: Pageable): Page<Task>

    @Query(
        """
    SELECT t FROM Task t
    WHERE (:name IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%')))
      AND (:description IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :description, '%')))
      AND (:dateFrom IS NULL OR t.endDate >= :dateFrom)
      AND (:dateTo IS NULL OR t.startDate <= :dateTo)
      AND (:recurringPattern IS NULL OR t.recurringPattern = :recurringPattern)
      AND (:status IS NULL OR t.status = :status)
      AND (:calendarId IS NULL OR t.calendar.id = :calendarId)
      AND (:categoryId IS NULL OR t.category.id = :categoryId)
    """
    )
    fun filter(
        @Param("name") name: String?,
        @Param("description") description: String?,
        @Param("dateFrom") dateFrom: LocalDateTime?,
        @Param("dateTo") dateTo: LocalDateTime?,
        @Param("recurringPattern") recurringPattern: RecurringPattern?,
        @Param("status") status: TaskStatus?,
        @Param("calendarId") calendarId: UUID?,
        @Param("categoryId") categoryId: UUID?,
        pageable: Pageable
    ): Page<Task>

}
