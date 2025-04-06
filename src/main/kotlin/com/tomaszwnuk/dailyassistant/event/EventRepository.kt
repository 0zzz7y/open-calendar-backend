package com.tomaszwnuk.dailyassistant.event

import com.tomaszwnuk.dailyassistant.domain.RecurringPattern
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.*

interface EventRepository : JpaRepository<Event, UUID> {

    fun findAllByCalendarId(
        calendarId: UUID,
        pageable: Pageable
    ): Page<Event>

    fun findAllByCategoryId(
        categoryId: UUID,
        pageable: Pageable
    ): Page<Event>

    @Query(
        """
    SELECT e FROM Event e
    WHERE (:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')))
      AND (:description IS NULL OR LOWER(e.description) LIKE LOWER(CONCAT('%', :description, '%')))
      AND (:calendarId IS NULL OR e.calendar.id = :calendarId)
      AND (:categoryId IS NULL OR e.category.id = :categoryId)
      AND (:recurringPattern IS NULL OR e.recurringPattern = :recurringPattern)
      AND (:dateFrom IS NULL OR e.startDate >= :dateFrom)
      AND (:dateTo IS NULL OR e.endDate <= :dateTo)
    """
    )
    fun filter(
        @Param("name") name: String?,
        @Param("description") description: String?,
        @Param("dateFrom") dateFrom: LocalDateTime?,
        @Param("dateTo") dateTo: LocalDateTime?,
        @Param("categoryId") categoryId: UUID?,
        @Param("calendarId") calendarId: UUID?,
        @Param("recurringPattern") recurringPattern: RecurringPattern?,
        pageable: Pageable
    ): Page<Event>

}
