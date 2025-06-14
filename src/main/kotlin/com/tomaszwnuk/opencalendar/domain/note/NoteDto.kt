package com.tomaszwnuk.opencalendar.domain.note

import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.DESCRIPTION_MAXIMUM_LENGTH
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.NAME_MAXIMUM_LENGTH
import com.tomaszwnuk.opencalendar.domain.record.RecordDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.*

/**
 * The note data transfer object.
 */
data class NoteDto(

    /**
     * The unique identifier of the note.
     */
    override val id: UUID? = null,

    /**
     * The name of the note.
     */
    @field:Size(
        max = NAME_MAXIMUM_LENGTH,
        message = "Name cannot be longer than $NAME_MAXIMUM_LENGTH characters."
    )
    override val name: String? = null,

    /**
     * The description of the note.
     */
    @field:NotBlank(message = "Description cannot be blank.")
    @field:Size(
        max = DESCRIPTION_MAXIMUM_LENGTH,
        message = "Description cannot be longer than $DESCRIPTION_MAXIMUM_LENGTH characters."
    )
    override val description: String,

    /**
     * The unique identifier of the calendar to which the note belongs.
     */
    @field:NotNull(message = "Calendar ID is required.")
    override val calendarId: UUID,

    /**
     * The unique identifier of the category associated with the note.
     */
    override val categoryId: UUID? = null

) : RecordDto(
    id = id,
    name = name,
    description = description,
    calendarId = calendarId,
    categoryId = categoryId
)
