package com.tomaszwnuk.dailyassistant.note

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface NoteRepository : JpaRepository<Note, UUID> {

    fun findAllByCalendarId(calendarId: UUID, pageable: Pageable): Page<Note>

    fun findAllByCategoryId(categoryId: UUID, pageable: Pageable): Page<Note>

    @Query(
        """
    SELECT n FROM Note n
    WHERE (:name IS NULL OR LOWER(n.name) LIKE LOWER(CONCAT('%', :name, '%')))
      AND (:description IS NULL OR LOWER(n.description) LIKE LOWER(CONCAT('%', :description, '%')))
      AND (:calendarId IS NULL OR n.calendar.id = :calendarId)
      AND (:categoryId IS NULL OR n.category.id = :categoryId)
    """
    )
    fun filter(
        @Param("name") name: String?,
        @Param("description") description: String?,
        @Param("calendarId") calendarId: UUID?,
        @Param("categoryId") categoryId: UUID?,
        pageable: Pageable
    ): Page<Note>

}
