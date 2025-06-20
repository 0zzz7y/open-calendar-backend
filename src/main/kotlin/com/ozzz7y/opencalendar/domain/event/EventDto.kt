package com.ozzz7y.opencalendar.domain.event

import com.ozzz7y.opencalendar.domain.field.FieldConstraints.DESCRIPTION_MAXIMUM_LENGTH
import com.ozzz7y.opencalendar.domain.field.FieldConstraints.NAME_MAXIMUM_LENGTH
import com.ozzz7y.opencalendar.domain.record.RecordDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.*

/**
 * The event data transfer object.
 */
data class EventDto(

    /**
     * The unique identifier of the event.
     */
    override val id: UUID? = null,

    /**
     * The title of the event.
     */
    @field:NotBlank(message = "Title cannot be blank.")
    @field:Size(
        max = NAME_MAXIMUM_LENGTH,
        message = "Title cannot be longer than $NAME_MAXIMUM_LENGTH characters."
    )
    override val name: String,

    /**
     * The description of the event.
     */
    @field:Size(
        max = DESCRIPTION_MAXIMUM_LENGTH,
        message = "Description cannot be longer than $DESCRIPTION_MAXIMUM_LENGTH characters."
    )
    override val description: String? = null,

    /**
     * The start date and time of the event.
     */
    @field:NotNull(message = "Start date is required.")
    override val startDate: LocalDateTime,

    /**
     * The end date and time of the event.
     */
    @field:NotNull(message = "End date is required.")
    override val endDate: LocalDateTime,

    /**
     * The recurring pattern of the event.
     */
    @field:NotNull(message = "Recurring pattern is required.")
    override val recurringPattern: RecurringPattern = RecurringPattern.NONE,

    /**
     * The unique identifier of the calendar to which the event belongs.
     */
    @field:NotNull(message = "Calendar ID is required.")
    override val calendarId: UUID,

    /**
     * The unique identifier of the category associated with the event.
     */
    override val categoryId: UUID? = null

) : RecordDto(
    id = id,
    name = name,
    description = description,
    categoryId = categoryId
), Schedulable
