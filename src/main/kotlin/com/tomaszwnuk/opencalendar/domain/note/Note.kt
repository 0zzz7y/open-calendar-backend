package com.tomaszwnuk.opencalendar.domain.note

import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_DESCRIPTION
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_ID
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_NAME
import com.tomaszwnuk.opencalendar.domain.record.Record
import jakarta.persistence.*
import java.util.*

/**
 * The note entity.
 */
@Entity
@Table(name = "note")
data class Note(

    /**
     * The unique identifier of the note.
     */
    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    override val id: UUID = UUID.randomUUID(),

    /**
     * The name of the note.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_NAME, nullable = true)
    override val name: String? = null,

    /**
     * The description of the note.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_DESCRIPTION, nullable = false)
    override val description: String,

    /**
     * The calendar to which the note belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    override val calendar: Calendar,

    /**
     * The category which the note is associated with.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    override val category: Category? = null

) : Record(
    id = id,
    name = name,
    description = description,
    calendar = calendar,
    category = category
) {

    /**
     * Converts the note entity to a data transfer object.
     *
     * @return The data transfer object representing the note.
     */
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
