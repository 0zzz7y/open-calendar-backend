package com.tomaszwnuk.dailyassistant.domain.entry

import com.tomaszwnuk.dailyassistant.category.Category
import com.tomaszwnuk.dailyassistant.domain.entity.Entity
import jakarta.persistence.*

@MappedSuperclass
abstract class Entry(

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    val name: String? = null,

    @Column(columnDefinition = "TEXT", nullable = true)
    val description: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    val category: Category? = null

) : Entity() {

    fun toDto(): EntryDto {
        return EntryDto(
            id = id,
            name = name,
            description = description,
            categoryId = category?.id
        )
    }

}
