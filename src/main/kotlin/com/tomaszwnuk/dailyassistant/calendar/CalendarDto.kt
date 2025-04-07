package com.tomaszwnuk.dailyassistant.calendar

import com.tomaszwnuk.dailyassistant.validation.FieldConstraints.NAME_MAXIMUM_LENGTH
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

data class CalendarDto(

    val id: UUID? = null,

    @field:NotBlank(message = "Name cannot be blank.")
    @field:Size(
        max = NAME_MAXIMUM_LENGTH,
        message = "Description cannot be longer than $NAME_MAXIMUM_LENGTH characters."
    )
    val name: String

)
