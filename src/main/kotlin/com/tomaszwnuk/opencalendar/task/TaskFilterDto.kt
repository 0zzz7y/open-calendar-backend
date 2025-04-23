package com.tomaszwnuk.opencalendar.task

import com.tomaszwnuk.opencalendar.domain.RecurringPattern
import java.time.LocalDateTime
import java.util.*

data class TaskFilterDto(

    val name: String? = null,

    val description: String? = null,

    val dateFrom: LocalDateTime? = null,

    val dateTo: LocalDateTime? = null,

    val recurringPattern: RecurringPattern? = null,

    val status: TaskStatus? = null,

    val calendarId: UUID? = null,

    val categoryId: UUID? = null

)
