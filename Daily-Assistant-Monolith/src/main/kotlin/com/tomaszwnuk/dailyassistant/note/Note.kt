package com.tomaszwnuk.dailyassistant.note

import com.tomaszwnuk.dailyassistant.category.Category
import com.tomaszwnuk.dailyassistant.domain.entry.Entry
import jakarta.persistence.*

@Entity
@Table(name = "note")
data class Note(

    @Column(columnDefinition = "VARCHAR(255)", nullable = true)
    override val name: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    override val description: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    override val category: Category? = null

) : Entry(name, description, category) {

    override fun toDto(): NoteDto {
        return NoteDto(
            id = id,
            name = name,
            description = description,
            categoryId = category?.id
        )
    }

}
