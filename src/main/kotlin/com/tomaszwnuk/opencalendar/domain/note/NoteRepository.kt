package com.tomaszwnuk.opencalendar.domain.note

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

/**
 * The repository for managing notes data.
 */
interface NoteRepository : JpaRepository<Note, UUID> {

    /**
     * Finds a note by its unique identifier and the user identifier of the calendar it belongs to.
     *
     * @param id The unique identifier of the note
     * @param userId The unique identifier of the user who owns the calendar
     *
     * @return An optional containing the note if found, or empty if not found
     */
    @Query(
        """
    SELECT n FROM Note n
    WHERE (n.id = :id)
      AND (n.calendar.userId = :userId)
    """
    )
    fun findByIdAndCalendarUserId(
        @Param("id") id: UUID,
        @Param("userId") userId: UUID
    ): Optional<Note>

    /**
     * Finds all notes that belong to a specific calendar user.
     *
     * @param userId The unique identifier of the user who owns the calendar
     *
     * @return A list of notes belonging to the specified calendar user
     */
    @Query(
        """
    SELECT n FROM Note n
    WHERE (n.calendar.userId = :userId)
    """
    )
    fun findAllByCalendarUserId(
        @Param("userId") userId: UUID
    ): List<Note>

    /**
     * Finds all notes that belong to a specific calendar and the user who owns it.
     *
     * @param calendarId The unique identifier of the calendar
     * @param userId The unique identifier of the user who owns the calendar
     *
     * @return A list of notes belonging to the specified calendar and user
     */
    @Query(
        """
    SELECT n FROM Note n
    WHERE (n.calendar.id = :calendarId)
      AND (n.calendar.userId = :userId)
    """
    )
    fun findAllByCalendarIdAndUserId(
        @Param("calendarId") calendarId: UUID,
        @Param("userId") userId: UUID
    ): List<Note>

    /**
     * Finds all notes that belong to a specific category and the user who owns the calendar.
     *
     * @param categoryId The unique identifier of the category
     * @param userId The unique identifier of the user who owns the calendar
     *
     * @return A list of notes belonging to the specified category and user
     */
    @Query(
        """
    SELECT n FROM Note n
    WHERE (n.category.id = :categoryId)
      AND (n.calendar.userId = :userId)
    """
    )
    fun findAllByCategoryIdAndUserId(
        @Param("categoryId") categoryId: UUID,
        @Param("userId") userId: UUID
    ): List<Note>

    /**
     * Filters notes based on provided criteria.
     *
     * @param userId The unique identifier of the user who owns the calendar
     * @param name The name of the note (optional)
     * @param description The description of the note (optional)
     * @param calendarId The unique identifier of the calendar (optional)
     * @param categoryId The unique identifier of the category (optional)
     *
     * @return A list of notes that match the specified criteria
     */
    @Query(
        """
    SELECT n FROM Note n
    WHERE (n.calendar.userId = :userId)
      AND (:name IS NULL OR LOWER(n.name) LIKE LOWER(CONCAT('%', :name, '%')))
      AND (:description IS NULL OR LOWER(n.description) LIKE LOWER(CONCAT('%', :description, '%')))
      AND (:calendarId IS NULL OR n.calendar.id = :calendarId)
      AND (:categoryId IS NULL OR n.category.id = :categoryId)
    """
    )
    fun filter(
        @Param("userId") userId: UUID,
        @Param("name") name: String?,
        @Param("description") description: String?,
        @Param("calendarId") calendarId: UUID?,
        @Param("categoryId") categoryId: UUID?
    ): List<Note>

}
