package com.tomaszwnuk.dailyassistant.event

import com.tomaszwnuk.dailyassistant.domain.RecurringPattern
import com.tomaszwnuk.dailyassistant.domain.entry.EntryDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.*

data class EventDto(

    override val id: UUID? = null,

    @field:NotBlank(message = "Name cannot be blank.")
    @field:Size(max = 255, message = "Name cannot be longer than 255 characters.")
    override val name: String,

    @field:Size(max = 4096, message = "Description cannot be longer than 4096 characters.")
    override val description: String? = null,

    @field:NotNull(message = "Start date is required.")
    val startDate: LocalDateTime,

    @field:NotNull(message = "End date is required.")
    val endDate: LocalDateTime,

    @field:NotNull(message = "Recurring pattern is required.")
    val recurringPattern: String = RecurringPattern.NONE.value,

    @field:NotNull(message = "Calendar ID is required.")
    val calendarId: UUID,

    override val categoryId: UUID? = null

) : EntryDto(id, name, description, categoryId)
