package com.ozzz7y.opencalendar.domain.task

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

/**
 * The repository for managing tasks data.
 */
interface TaskRepository : JpaRepository<Task, UUID> {

    /**
     * Finds a task by its unique identifier and the user identifier of the calendar it belongs to.
     *
     * @param id The unique identifier of the task
     * @param userId The unique identifier of the user who owns the calendar
     *
     * @return An optional containing the task if found, or empty if not found
     */
    @Query(
        """
    SELECT t FROM Task t
    WHERE (t.id = :id)
      AND (t.calendar.userId = :userId)
    """
    )
    fun findByIdAndCalendarUserId(@Param("id") id: UUID, @Param("userId") userId: UUID): Optional<Task>

    /**
     * Finds all tasks that belong to a specific calendar user.
     *
     * @param userId The unique identifier of the user who owns the calendar
     *
     * @return A list of tasks belonging to the specified calendar user
     */
    @Query(
        """
    SELECT t FROM Task t
    WHERE (t.calendar.userId = :userId)
    """
    )
    fun findAllByCalendarUserId(@Param("userId") userId: UUID): List<Task>

    /**
     * Finds all tasks that belong to a specific calendar and the user who owns it.
     *
     * @param calendarId The unique identifier of the calendar
     * @param userId The unique identifier of the user who owns the calendar
     *
     * @return A list of tasks belonging to the specified calendar and user
     */
    @Query(
        """
    SELECT t FROM Task t
    WHERE (t.calendar.id = :calendarId)
      AND (t.calendar.userId = :userId)
    """
    )
    fun findAllByCalendarIdAndCalendarUserId(
        @Param("calendarId") calendarId: UUID,
        @Param("userId") userId: UUID
    ): List<Task>

    /**
     * Finds all tasks that belong to a specific category and the user who owns the calendar.
     *
     * @param categoryId The unique identifier of the category
     * @param userId The unique identifier of the user who owns the calendar
     *
     * @return A list of tasks belonging to the specified category and user
     */
    @Query(
        """
    SELECT t FROM Task t
    WHERE (t.category.id = :categoryId)
      AND (t.calendar.userId = :userId)
    """
    )
    fun findAllByCategoryIdAndCalendarUserId(
        @Param("categoryId") categoryId: UUID,
        @Param("userId") userId: UUID
    ): List<Task>

    /**
     * Filters tasks based on provided criteria.
     *
     * @param userId The unique identifier of the user who owns the calendar
     * @param name The name of the task (optional)
     * @param description The description of the task (optional)
     * @param status The status of the task (optional)
     * @param calendarId The unique identifier of the calendar (optional)
     * @param categoryId The unique identifier of the category (optional)
     *
     * @return A list of tasks that match the specified criteria
     */
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
