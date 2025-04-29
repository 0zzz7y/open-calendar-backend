package com.tomaszwnuk.opencalendar.event

import com.tomaszwnuk.opencalendar.domain.RecurringPattern
import com.tomaszwnuk.opencalendar.domain.Schedulable
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.DESCRIPTION_MAXIMUM_LENGTH
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.TITLE_MAXIMUM_LENGTH
import com.tomaszwnuk.opencalendar.domain.record.RecordDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.*

data class EventDto(

    override val id: UUID? = null,

    @field:NotBlank(message = "Title cannot be blank.")
    @field:Size(
        max = TITLE_MAXIMUM_LENGTH,
        message = "Title cannot be longer than $TITLE_MAXIMUM_LENGTH characters."
    )
    override val title: String,

    @field:Size(
        max = DESCRIPTION_MAXIMUM_LENGTH,
        message = "Description cannot be longer than $DESCRIPTION_MAXIMUM_LENGTH characters."
    )
    override val description: String? = null,

    @field:NotNull(message = "Start date is required.")
    override val startDate: LocalDateTime,

    @field:NotNull(message = "End date is required.")
    override val endDate: LocalDateTime,

    @field:NotNull(message = "Recurring pattern is required.")
    override val recurringPattern: RecurringPattern = RecurringPattern.NONE,

    @field:NotNull(message = "Calendar ID is required.")
    override val calendarId: UUID,

    override val categoryId: UUID? = null

) : RecordDto(
    id = id,
    title = title,
    description = description,
    categoryId = categoryId
), Schedulable
