package com.tomaszwnuk.dailyassistant.category

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "category")
data class Category(

    @Id
    @Column(columnDefinition = "CHAR(36)", nullable = false)
    override val id: UUID = UUID.randomUUID(),

    @Column(columnDefinition = "VARCHAR(255)", unique = true, nullable = false)
    val name: String,

    @Column(columnDefinition = "VARCHAR(7)", nullable = false)
    val color: String = CategoryColors.DEFAULT_COLOR,

) : com.tomaszwnuk.dailyassistant.domain.Entity() {

    fun toDto(): CategoryDto {
        return CategoryDto(
            id = id,
            name = name,
            color = color
        )
    }

}
