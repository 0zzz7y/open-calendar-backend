/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.event

import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.DESCRIPTION_MAXIMUM_LENGTH
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.TITLE_MAXIMUM_LENGTH
import com.tomaszwnuk.opencalendar.domain.other.RecurringPattern
import com.tomaszwnuk.opencalendar.domain.other.Schedulable
import com.tomaszwnuk.opencalendar.domain.record.RecordDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.*

/**
 * Data Transfer Object (DTO) for representing an event.
 * Contains validation constraints and fields required for transferring event data.
 *
 * @property id The unique identifier of the event. Can be null for new events.
 * @property title The title of the event. Cannot be blank and has a maximum length defined by TITLE_MAXIMUM_LENGTH.
 * @property description An optional description of the event with a maximum length defined by DESCRIPTION_MAXIMUM_LENGTH.
 * @property startDate The start date and time of the event. Cannot be null.
 * @property endDate The end date and time of the event. Cannot be null.
 * @property recurringPattern The recurring pattern of the event. Defaults to NONE and cannot be null.
 * @property calendarId The ID of the calendar to which the event belongs. Cannot be null.
 * @property categoryId An optional ID of the category associated with the event.
 */
data class EventDto(

    /**
     * The unique identifier of the event.
     * Can be null for new events.
     */
    override val id: UUID? = null,

    /**
     * The title of the event.
     * Cannot be blank and has a maximum length defined by TITLE_MAXIMUM_LENGTH.
     */
    @field:NotBlank(message = "Title cannot be blank.")
    @field:Size(
        max = TITLE_MAXIMUM_LENGTH,
        message = "Title cannot be longer than $TITLE_MAXIMUM_LENGTH characters."
    )
    override val title: String,

    /**
     * An optional description of the event.
     * Has a maximum length defined by DESCRIPTION_MAXIMUM_LENGTH.
     */
    @field:Size(
        max = DESCRIPTION_MAXIMUM_LENGTH,
        message = "Description cannot be longer than $DESCRIPTION_MAXIMUM_LENGTH characters."
    )
    override val description: String? = null,

    /**
     * The start date and time of the event.
     * Cannot be null.
     */
    @field:NotNull(message = "Start date is required.")
    override val startDate: LocalDateTime,

    /**
     * The end date and time of the event.
     * Cannot be null.
     */
    @field:NotNull(message = "End date is required.")
    override val endDate: LocalDateTime,

    /**
     * The recurring pattern of the event.
     * Defaults to NONE and cannot be null.
     */
    @field:NotNull(message = "Recurring pattern is required.")
    override val recurringPattern: RecurringPattern = RecurringPattern.NONE,

    /**
     * The ID of the calendar to which the event belongs.
     * Cannot be null.
     */
    @field:NotNull(message = "Calendar ID is required.")
    override val calendarId: UUID,

    /**
     * An optional ID of the category associated with the event.
     */
    override val categoryId: UUID? = null

) : RecordDto(
    id = id,
    title = title,
    description = description,
    categoryId = categoryId
), Schedulable
