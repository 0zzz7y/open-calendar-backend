package com.tomaszwnuk.dailyassistant.event

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface EventRepository : JpaRepository<Event, UUID> {

    fun findAllByCalendarId(calendarId: UUID): List<Event>

    fun findAllByCategoryId(categoryId: UUID): List<Event>

    fun findAllByCalendarIdAndCategoryId(calendarId: UUID, categoryId: UUID): List<Event>
}
