package com.tomaszwnuk.dailyassistant.event

import com.tomaszwnuk.dailyassistant.domain.RecurringPattern
import com.tomaszwnuk.dailyassistant.domain.entry.EntryDto
import java.time.LocalDateTime
import java.util.*

data class EventDto(

    override val id: UUID? = null,

    override val name: String,

    override val description: String? = null,

    val date: LocalDateTime,

    val recurringPattern: RecurringPattern = RecurringPattern.NONE,

    val calendarId: UUID,

    override val categoryId: UUID? = null

) : EntryDto(id, name, description, categoryId)
