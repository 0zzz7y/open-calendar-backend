package com.tomaszwnuk.opencalendar.domain.category

import com.tomaszwnuk.opencalendar.domain.entity.EntityDto
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLOR_HEX_LENGTH
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.TITLE_MAXIMUM_LENGTH
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.util.*

data class CategoryDto(

    override val id: UUID? = null,

    @field:NotBlank(message = "Title cannot be blank.")
    @field:Size(
        max = TITLE_MAXIMUM_LENGTH,
        message = "Title cannot be longer than $TITLE_MAXIMUM_LENGTH characters."
    )
    val title: String,

    @field:Size(min = COLOR_HEX_LENGTH, max = COLOR_HEX_LENGTH, message = "Color must be a valid hex color code.")
    val color: String = CategoryColorHelper.DEFAULT_COLOR

) : EntityDto(id = id), Serializable
