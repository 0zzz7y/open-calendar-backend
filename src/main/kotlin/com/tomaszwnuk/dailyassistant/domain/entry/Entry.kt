package com.tomaszwnuk.dailyassistant.domain.entry

import com.tomaszwnuk.dailyassistant.category.Category
import com.tomaszwnuk.dailyassistant.domain.entity.Entity
import com.tomaszwnuk.dailyassistant.validation.FieldConstraints.COLUMN_DEFINITION_DESCRIPTION
import com.tomaszwnuk.dailyassistant.validation.FieldConstraints.COLUMN_DEFINITION_ID
import jakarta.persistence.*
import java.util.*

@MappedSuperclass
abstract class Entry(

    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    override val id: UUID = UUID.randomUUID(),

    @Column(columnDefinition = COLUMN_DEFINITION_DESCRIPTION, nullable = true)
    open val name: String? = null,

    @Column(columnDefinition = COLUMN_DEFINITION_DESCRIPTION, nullable = true)
    open val description: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    open val category: Category? = null

) : Entity(id = id) {

    fun toDto(): EntryDto {
        return EntryDto(
            id = id,
            name = name,
            description = description,
            categoryId = category?.id
        )
    }

}
