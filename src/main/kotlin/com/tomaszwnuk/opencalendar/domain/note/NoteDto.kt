/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.note

import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.DESCRIPTION_MAXIMUM_LENGTH
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.TITLE_MAXIMUM_LENGTH
import com.tomaszwnuk.opencalendar.domain.record.RecordDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.*

/**
 * Data Transfer Object (DTO) for a Note.
 * Represents the data structure used to transfer note information between layers.
 *
 * @property id The unique identifier of the note (optional).
 * @property title The title of the note (optional, maximum length defined by TITLE_MAXIMUM_LENGTH).
 * @property description The description of the note (mandatory, cannot be blank, maximum length defined by DESCRIPTION_MAXIMUM_LENGTH).
 * @property calendarId The unique identifier of the calendar associated with the note (mandatory).
 * @property categoryId The unique identifier of the category associated with the note (optional).
 */
data class NoteDto(

    /**
     * The unique identifier of the note.
     * This field is optional and can be null.
     */
    override val id: UUID? = null,

    /**
     * The title of the note.
     * This field is optional and has a maximum length constraint.
     */
    @field:Size(
        max = TITLE_MAXIMUM_LENGTH,
        message = "Title cannot be longer than $TITLE_MAXIMUM_LENGTH characters."
    )
    override val title: String? = null,

    /**
     * The description of the note.
     * This field is mandatory, cannot be blank, and has a maximum length constraint.
     */
    @field:NotBlank(message = "Description cannot be blank.")
    @field:Size(
        max = DESCRIPTION_MAXIMUM_LENGTH,
        message = "Description cannot be longer than $DESCRIPTION_MAXIMUM_LENGTH characters."
    )
    override val description: String,

    /**
     * The unique identifier of the calendar associated with the note.
     * This field is mandatory and cannot be null.
     */
    @field:NotNull(message = "Calendar ID is required.")
    override val calendarId: UUID,

    /**
     * The unique identifier of the category associated with the note.
     * This field is optional and can be null.
     */
    override val categoryId: UUID? = null

) : RecordDto(
    id = id,
    title = title,
    description = description,
    calendarId = calendarId,
    categoryId = categoryId
)
