package com.tomaszwnuk.opencalendar.category

import com.tomaszwnuk.opencalendar.domain.entity.EntityDto
import com.tomaszwnuk.opencalendar.validation.FieldConstraints.COLOR_HEX_LENGTH
import com.tomaszwnuk.opencalendar.validation.FieldConstraints.NAME_MAXIMUM_LENGTH
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

data class CategoryDto(

    override val id: UUID? = null,

    @field:NotBlank(message = "Name cannot be blank.")
    @field:Size(
        max = NAME_MAXIMUM_LENGTH,
        message = "Description cannot be longer than $NAME_MAXIMUM_LENGTH characters."
    )
    val name: String,

    @field:Size(min = COLOR_HEX_LENGTH, max = COLOR_HEX_LENGTH, message = "Color must be a valid hex color code.")
    val color: String = CategoryColors.DEFAULT

) : EntityDto(id = id)
