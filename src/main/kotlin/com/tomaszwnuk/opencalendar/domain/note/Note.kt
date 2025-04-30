/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.note

import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_DESCRIPTION
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_ID
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_TITLE
import com.tomaszwnuk.opencalendar.domain.record.Record
import jakarta.persistence.*
import java.util.*

/**
 * Entity class representing a Note.
 * A Note is associated with a Calendar and optionally a Category.
 *
 * @property id The unique identifier of the note.
 * @property title The title of the note (optional).
 * @property description The description of the note.
 * @property calendar The calendar to which the note belongs.
 * @property category The category of the note (optional).
 */
@Entity
@Table(name = "note")
data class Note(

    /**
     * The unique identifier of the note.
     * This field is immutable and generated automatically.
     */
    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    override val id: UUID = UUID.randomUUID(),

    /**
     * The title of the note.
     * This field is optional and can be null.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_TITLE, nullable = true)
    override val title: String? = null,

    /**
     * The description of the note.
     * This field is mandatory and cannot be null.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_DESCRIPTION, nullable = false)
    override val description: String,

    /**
     * The calendar to which the note belongs.
     * This field is mandatory and cannot be null.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    override val calendar: Calendar,

    /**
     * The category of the note.
     * This field is optional and can be null.
     */
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

    /**
     * Converts the Note entity to a Data Transfer Object (DTO).
     *
     * @return A NoteDto object containing the note's data.
     */
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
