/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.calendar

import com.tomaszwnuk.opencalendar.domain.entity.EntityDto
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.EMOJI_MAXIMUM_LENGTH
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.TITLE_MAXIMUM_LENGTH
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

/**
 * Data Transfer Object (DTO) for the `Calendar` entity.
 * Represents the data structure used for transferring calendar information.
 *
 * @property id The unique identifier of the calendar (optional).
 * @property title The title of the calendar. Cannot be blank and has a maximum length constraint.
 * @property emoji The emoji associated with the calendar. Has a maximum length constraint.
 */
data class CalendarDto(

    /**
     * The unique identifier of the calendar.
     * This field is optional and can be null.
     */
    override val id: UUID? = null,

    /**
     * The title of the calendar.
     * This field is mandatory and cannot be blank. It also has a maximum length constraint.
     */
    @field:NotBlank(message = "Title cannot be blank.")
    @field:Size(
        max = TITLE_MAXIMUM_LENGTH,
        message = "Title cannot be longer than $TITLE_MAXIMUM_LENGTH characters."
    )
    val title: String,

    /**
     * The emoji associated with the calendar.
     * This field has a maximum length constraint.
     */
    @field:Size(
        max = EMOJI_MAXIMUM_LENGTH,
        message = "Emoji cannot be longer than $EMOJI_MAXIMUM_LENGTH characters."
    )
    val emoji: String

) : EntityDto(id = id)
