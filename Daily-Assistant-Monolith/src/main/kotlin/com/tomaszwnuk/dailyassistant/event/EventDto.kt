package com.tomaszwnuk.dailyassistant.event

import com.tomaszwnuk.dailyassistant.domain.RecurringPattern
import java.time.LocalDateTime
import java.util.UUID

data class EventDto(

    val id: UUID? = null,

    val title: String,

    val description: String? = null,

    val date: LocalDateTime,

    val recurringPattern: RecurringPattern = RecurringPattern.NONE,

    val calendarId: UUID,

    val categoryId: UUID? = null
)
