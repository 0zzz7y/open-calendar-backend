package com.tomaszwnuk.dailyassistant.calendar

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

data class CalendarDto(

    val id: UUID,

    @field:NotBlank(message = "Name cannot be blank.")
    @field:Size(max = 255, message = "Name cannot be longer than 255 characters.")
    val name: String

)
