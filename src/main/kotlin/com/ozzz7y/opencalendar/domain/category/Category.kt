package com.ozzz7y.opencalendar.domain.category

import com.ozzz7y.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_COLOR
import com.ozzz7y.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_ID
import com.ozzz7y.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_NAME
import jakarta.persistence.*
import java.util.*

/**
 * The category entity.
 */
@Entity
@Table(name = "category")
data class Category(

    /**
     * The unique identifier of the category.
     */
    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    override val id: UUID = UUID.randomUUID(),

    /**
     * The name of the category.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_NAME, unique = true, nullable = false)
    val name: String,

    /**
     * The color of the category.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_COLOR, nullable = false)
    val color: String = CategoryColorHelper.DEFAULT_COLOR,

    /**
     * The unique identifier of the user who owns the category.
     */
    @JoinColumn(name = "user_id", unique = false, nullable = false)
    val userId: UUID

) : com.ozzz7y.opencalendar.domain.entity.Entity() {

    /**
     * Converts the category entity to a data transfer object.
     *
     * @return The data transfer object representing the category.
     */
    fun toDto(): CategoryDto {
        return CategoryDto(
            id = id,
            name = name,
            color = color
        )
    }

}
