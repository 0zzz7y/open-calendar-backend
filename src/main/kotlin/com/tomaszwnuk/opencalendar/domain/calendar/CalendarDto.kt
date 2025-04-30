package com.tomaszwnuk.opencalendar.domain.calendar

import com.tomaszwnuk.opencalendar.domain.entity.EntityDto
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.EMOJI_MAXIMUM_LENGTH
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.TITLE_MAXIMUM_LENGTH
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

data class CalendarDto(

    override val id: UUID? = null,

    @field:NotBlank(message = "Title cannot be blank.")
    @field:Size(
        max = TITLE_MAXIMUM_LENGTH,
        message = "Title cannot be longer than $TITLE_MAXIMUM_LENGTH characters."
    )
    val title: String,

    @field:Size(
        max = EMOJI_MAXIMUM_LENGTH,
        message = "Emoji cannot be longer than $EMOJI_MAXIMUM_LENGTH characters."
    )
    val emoji: String

) : EntityDto(id = id)
