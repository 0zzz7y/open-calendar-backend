package com.tomaszwnuk.dailyassistant.category

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

class CategoryDto(

    val id: UUID? = null,

    @field:NotBlank(message = "Name cannot be blank.")
    @field:Size(max = 255, message = "Name cannot be longer than 255 characters.")
    val name: String,

    val color: String = CategoryColors.DEFAULT

)
