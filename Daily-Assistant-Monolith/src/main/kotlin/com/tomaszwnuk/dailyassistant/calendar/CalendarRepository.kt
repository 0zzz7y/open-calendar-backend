package com.tomaszwnuk.dailyassistant.calendar

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface CalendarRepository : JpaRepository<Calendar, UUID> {

    fun existsByName(name: String): Boolean

    @Query(
        """
    SELECT c FROM Calendar c
    WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """
    )
    fun filter(
        @Param("name") name: String?,
        pageable: Pageable
    ): Page<Calendar>

}
