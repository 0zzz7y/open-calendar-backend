package com.tomaszwnuk.opencalendar.domain.event

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.*

interface EventRepository : JpaRepository<Event, UUID> {

    @Query(
        """
    SELECT e FROM Event e
    WHERE (e.id = :id)
      AND (e.calendar.userId = :userId)
    """
    )
    fun findByIdAndCalendarUserId(@Param("id") id: UUID, @Param("userId") userId: UUID): Optional<Event>

    @Query(
        """
    SELECT e FROM Event e
    WHERE (e.calendar.userId = :userId)
    """
    )
    fun findAllByCalendarUserId(@Param("userId") userId: UUID): List<Event>

    @Query(
        """
    SELECT e FROM Event e
    WHERE (e.calendar.id = :calendarId)
      AND (e.calendar.userId = :userId)
    """
    )
    fun findAllByCalendarIdAndCalendarUserId(
        @Param("calendarId") calendarId: UUID,
        @Param("userId") userId: UUID
    ): List<Event>

    @Query(
        """
    SELECT e FROM Event e
    WHERE (e.category.id = :categoryId)
      AND e.calendar.userId = :userId
    """
    )
    fun findAllByCategoryIdAndCalendarUserId(
        @Param("categoryId") categoryId: UUID,
        @Param("userId") userId: UUID
    ): List<Event>

    @Query(
        """
    SELECT e FROM Event e
    WHERE (e.calendar.userId = :userId)
      AND (:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')))
      AND (:description IS NULL OR LOWER(e.description) LIKE LOWER(CONCAT('%', :description, '%')))
      AND (:dateFrom IS NULL OR e.startDate >= :dateFrom)
      AND (:dateTo IS NULL OR e.endDate <= :dateTo)
      AND (:recurringPattern IS NULL OR e.recurringPattern = :recurringPattern)
      AND (:calendarId IS NULL OR e.calendar.id = :calendarId)
      AND (:categoryId IS NULL OR e.category.id = :categoryId)
    """
    )
    fun filter(
        @Param("userId") userId: UUID,
        @Param("name") name: String?,
        @Param("description") description: String?,
        @Param("dateFrom") dateFrom: LocalDateTime?,
        @Param("dateTo") dateTo: LocalDateTime?,
        @Param("recurringPattern") recurringPattern: RecurringPattern?,
        @Param("calendarId") calendarId: UUID?,
        @Param("categoryId") categoryId: UUID?
    ): List<Event>

}
