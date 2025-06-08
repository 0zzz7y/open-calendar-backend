package com.tomaszwnuk.opencalendar.domain.event

import java.time.LocalDateTime
import java.util.*

data class EventFilterDto(

    val name: String? = null,

    val description: String? = null,

    val dateFrom: LocalDateTime? = null,

    val dateTo: LocalDateTime? = null,

    val recurringPattern: RecurringPattern? = null,

    val calendarId: UUID? = null,

    val categoryId: UUID? = null

)
