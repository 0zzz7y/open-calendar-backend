package com.tomaszwnuk.opencalendar.domain.note

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface NoteRepository : JpaRepository<Note, UUID> {

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

    @Query(
        """
    SELECT n FROM Note n
    WHERE (n.calendar.userId = :userId)
    """
    )
    fun findAllByUserId(
        @Param("userId") userId: UUID
    ): List<Note>

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
