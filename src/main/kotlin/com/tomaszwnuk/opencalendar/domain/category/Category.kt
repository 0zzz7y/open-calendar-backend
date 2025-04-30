/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.category

import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_COLOR
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_ID
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_TITLE
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

/**
 * Entity class representing a category in the system.
 * Maps to the "category" table in the database.
 *
 * @property id The unique identifier of the category. Cannot be null or updated.
 * @property title The title of the category. Must be unique and cannot be null.
 * @property color The color associated with the category. Defaults to a predefined value.
 */
@Entity
@Table(name = "category")
data class Category(

    /**
     * The unique identifier of the category.
     * This field is generated automatically and cannot be updated.
     */
    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    override val id: UUID = UUID.randomUUID(),

    /**
     * The title of the category.
     * This field must be unique and cannot be null.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_TITLE, unique = true, nullable = false)
    val title: String,

    /**
     * The color associated with the category.
     * This field cannot be null and defaults to a predefined value.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_COLOR, nullable = false)
    val color: String = CategoryColorHelper.DEFAULT_COLOR

) : com.tomaszwnuk.opencalendar.domain.entity.Entity() {

    /**
     * Converts the `Category` entity to a `CategoryDto`.
     *
     * @return A `CategoryDto` containing the category's details.
     */
    fun toDto(): CategoryDto {
        return CategoryDto(
            id = id,
            title = title,
            color = color
        )
    }

}
