package com.tomaszwnuk.opencalendar.domain.category

import com.tomaszwnuk.opencalendar.domain.entity.EntityDto
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLOR_HEX_LENGTH
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.NAME_MAXIMUM_LENGTH
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

/**
 * The category data transfer object.
 */
data class CategoryDto(

    /**
     * The unique identifier of the category.
     */
    override val id: UUID? = null,

    /**
     * The name of the category.
     */
    @field:NotBlank(message = "Name cannot be blank.")
    @field:Size(
        max = NAME_MAXIMUM_LENGTH,
        message = "Name cannot be longer than $NAME_MAXIMUM_LENGTH characters."
    )
    val name: String,

    /**
     * The color of the category.
     */
    @field:Size(min = COLOR_HEX_LENGTH, max = COLOR_HEX_LENGTH, message = "Color must be a valid hex color code.")
    val color: String = CategoryColorHelper.DEFAULT_COLOR

) : EntityDto(id = id)
