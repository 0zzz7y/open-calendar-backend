/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.category

import com.tomaszwnuk.opencalendar.domain.entity.EntityDto
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLOR_HEX_LENGTH
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.TITLE_MAXIMUM_LENGTH
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

/**
 * Data Transfer Object (DTO) for the `Category` entity.
 * Used to transfer category data between layers of the application.
 *
 * @property id The unique identifier of the category. Optional for new categories.
 * @property title The title of the category. Cannot be blank and must not exceed the maximum length.
 * @property color The color associated with the category. Must be a valid hex color code.
 */
data class CategoryDto(

    /**
     * The unique identifier of the category.
     * This field is optional and is typically null for new categories.
     */
    override val id: UUID? = null,

    /**
     * The title of the category.
     * This field cannot be blank and must not exceed the maximum length defined in `FieldConstraints`.
     */
    @field:NotBlank(message = "Title cannot be blank.")
    @field:Size(
        max = TITLE_MAXIMUM_LENGTH,
        message = "Title cannot be longer than $TITLE_MAXIMUM_LENGTH characters."
    )
    val title: String,

    /**
     * The color associated with the category.
     * This field must be a valid hex color code with a fixed length defined in `FieldConstraints`.
     * Defaults to the application's predefined default color.
     */
    @field:Size(min = COLOR_HEX_LENGTH, max = COLOR_HEX_LENGTH, message = "Color must be a valid hex color code.")
    val color: String = CategoryColorHelper.DEFAULT_COLOR

) : EntityDto(id = id)
