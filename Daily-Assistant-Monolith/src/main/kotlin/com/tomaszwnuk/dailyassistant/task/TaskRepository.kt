package com.tomaszwnuk.dailyassistant.task

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TaskRepository : JpaRepository<Task, UUID> {

    fun findAllByCategoryId(categoryId: UUID): List<Task>

    fun findAllByCalendarId(calendarId: UUID): List<Task>

    fun findAllByCalendarIdAndCategoryId(calendarId: UUID, categoryId: UUID): List<Task>
}
