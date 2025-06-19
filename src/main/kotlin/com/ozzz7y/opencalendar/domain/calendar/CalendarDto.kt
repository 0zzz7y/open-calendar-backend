package com.ozzz7y.opencalendar.domain.calendar

import com.ozzz7y.opencalendar.domain.entity.EntityDto
import com.ozzz7y.opencalendar.domain.field.FieldConstraints.EMOJI_MAXIMUM_LENGTH
import com.ozzz7y.opencalendar.domain.field.FieldConstraints.NAME_MAXIMUM_LENGTH
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

/**
 * The calendar data transfer object.
 */
data class CalendarDto(

    /**
     * The unique identifier of the calendar.
     */
    override val id: UUID? = null,

    /**
     * The name of the calendar.
     */
    @field:NotBlank(message = "Name cannot be blank.")
    @field:Size(
        max = NAME_MAXIMUM_LENGTH,
        message = "Name cannot be longer than $NAME_MAXIMUM_LENGTH characters."
    )
    val name: String,

    /**
     * The emoji representing the calendar.
     */
    @field:Size(
        max = EMOJI_MAXIMUM_LENGTH,
        message = "Emoji cannot be longer than $EMOJI_MAXIMUM_LENGTH characters."
    )
    val emoji: String

) : EntityDto(id = id)
