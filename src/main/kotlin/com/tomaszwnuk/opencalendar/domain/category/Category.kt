package com.tomaszwnuk.opencalendar.domain.category

import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_COLOR
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_ID
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_NAME
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "category")
data class Category(

    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    override val id: UUID = UUID.randomUUID(),

    @Column(columnDefinition = COLUMN_DEFINITION_NAME, unique = true, nullable = false)
    val name: String,

    @Column(columnDefinition = COLUMN_DEFINITION_COLOR, nullable = false)
    val color: String = CategoryColorHelper.DEFAULT_COLOR,

    @JoinColumn(name = "user_id", unique = false, nullable = false)
    val userId: UUID

) : com.tomaszwnuk.opencalendar.domain.entity.Entity() {

    fun toDto(): CategoryDto {
        return CategoryDto(
            id = id,
            name = name,
            color = color
        )
    }

}
