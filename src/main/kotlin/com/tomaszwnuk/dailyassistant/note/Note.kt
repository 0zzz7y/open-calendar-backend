package com.tomaszwnuk.dailyassistant.note

import com.tomaszwnuk.dailyassistant.calendar.Calendar
import com.tomaszwnuk.dailyassistant.category.Category
import com.tomaszwnuk.dailyassistant.domain.entry.Entry
import com.tomaszwnuk.dailyassistant.validation.FieldConstraints.COLUMN_DEFINITION_DESCRIPTION
import com.tomaszwnuk.dailyassistant.validation.FieldConstraints.COLUMN_DEFINITION_ID
import com.tomaszwnuk.dailyassistant.validation.FieldConstraints.COLUMN_DEFINITION_NAME
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "note")
data class Note(

    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    override val id: UUID = UUID.randomUUID(),

    @Column(columnDefinition = COLUMN_DEFINITION_NAME, nullable = true)
    override val name: String? = null,

    @Column(columnDefinition = COLUMN_DEFINITION_DESCRIPTION, nullable = false)
    override val description: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    override val calendar: Calendar,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    override val category: Category? = null

) : Entry(
    id = id,
    name = name,
    description = description,
    calendar = calendar,
    category = category
) {

    override fun toDto(): NoteDto {
        return NoteDto(
            id = id,
            name = name,
            description = description,
            calendarId = calendar.id,
            categoryId = category?.id
        )
    }

}
