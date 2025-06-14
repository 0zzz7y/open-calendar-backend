package com.tomaszwnuk.opencalendar.domain.event

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.*

/**
 * The repository for managing events data.
 */
interface EventRepository : JpaRepository<Event, UUID> {

    /**
     * Finds an event by its unique identifier and the user identifier of the calendar it belongs to.
     *
     * @param id The unique identifier of the event
     * @param userId The unique identifier of the user who owns the calendar
     *
     * @return An optional containing the event if found, or empty if not found
     */
    @Query(
        """
    SELECT e FROM Event e
    WHERE (e.id = :id)
      AND (e.calendar.userId = :userId)
    """
    )
    fun findByIdAndCalendarUserId(@Param("id") id: UUID, @Param("userId") userId: UUID): Optional<Event>

    /**
     * Finds all events that belong to a specific calendar user.
     *
     * @param userId The unique identifier of the user who owns the calendar
     *
     * @return A list of events belonging to the specified calendar user
     */
    @Query(
        """
    SELECT e FROM Event e
    WHERE (e.calendar.userId = :userId)
    """
    )
    fun findAllByCalendarUserId(@Param("userId") userId: UUID): List<Event>

    /**
     * Finds all events that belong to a specific calendar and the user who owns it.
     *
     * @param calendarId The unique identifier of the calendar
     * @param userId The unique identifier of the user who owns the calendar
     *
     * @return A list of events belonging to the specified calendar and user
     */
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

    /**
     * Finds all events that belong to a specific category and the user who owns the calendar.
     *
     * @param categoryId The unique identifier of the category
     * @param userId The unique identifier of the user who owns the calendar
     *
     * @return A list of events belonging to the specified category and user
     */
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

    /**
     * Filters events based on provided criteria.
     *
     * @param userId The unique identifier of the user who owns the calendar
     * @param name The name of the event (optional)
     * @param description The description of the event (optional)
     * @param dateFrom The start date of the event (optional)
     * @param dateTo The end date of the event (optional)
     * @param recurringPattern The recurring pattern of the event (optional)
     * @param calendarId The unique identifier of the calendar (optional)
     * @param categoryId The unique identifier of the category (optional)
     *
     * @return A list of events that match the specified criteria
     */
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
