package com.ozzz7y.opencalendar.domain.calendar

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

/**
 * The repository for managing calendars data.
 */
interface CalendarRepository : JpaRepository<Calendar, UUID> {

    /**
     * Checks if a calendar with the given name and user ID exists.
     *
     * @param name The name of the calendar
     * @param userId The unique identifier of the user who owns the calendar
     *
     * @return true if a calendar with the given name and user ID exists, false otherwise
     */
    fun existsByNameAndUserId(name: String, userId: UUID): Boolean

    /**
     * Finds all calendars that belong to a specific user.
     *
     * @param userId The unique identifier of the user who owns the calendars
     *
     * @return A list of calendars belonging to the specified user
     */
    fun findAllByUserId(userId: UUID): List<Calendar>

    /**
     * Finds a calendar by its unique identifier and the user identifier of the calendar it belongs to.
     *
     * @param id The unique identifier of the calendar
     * @param userId The unique identifier of the user who owns the calendar
     *
     * @return An optional containing the calendar if found, or empty if not found
     */
    fun findByIdAndUserId(id: UUID, userId: UUID): Optional<Calendar>

    @Query(
        """
    SELECT c FROM Calendar c
    WHERE (c.userId = :userId)
      AND (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT("%", :name, "%")))
      AND (:emoji IS NULL OR c.emoji = :emoji)
    """,
        nativeQuery = false
    )
    fun filter(
        @Param("userId") userId: UUID,
        @Param("name") name: String?,
        @Param("emoji") emoji: String?
    ): List<Calendar>

}
