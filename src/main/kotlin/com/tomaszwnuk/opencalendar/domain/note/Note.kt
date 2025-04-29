package com.tomaszwnuk.opencalendar.domain.note

import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_DESCRIPTION
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_ID
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_TITLE
import com.tomaszwnuk.opencalendar.domain.record.Record
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "note")
data class Note(

    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    override val id: UUID = UUID.randomUUID(),

    @Column(columnDefinition = COLUMN_DEFINITION_TITLE, nullable = true)
    override val title: String? = null,

    @Column(columnDefinition = COLUMN_DEFINITION_DESCRIPTION, nullable = false)
    override val description: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    override val calendar: Calendar,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    override val category: Category? = null

) : Record(
    id = id,
    title = title,
    description = description,
    calendar = calendar,
    category = category
) {

    override fun toDto(): NoteDto {
        return NoteDto(
            id = id,
            title = title,
            description = description,
            calendarId = calendar.id,
            categoryId = category?.id
        )
    }

}
