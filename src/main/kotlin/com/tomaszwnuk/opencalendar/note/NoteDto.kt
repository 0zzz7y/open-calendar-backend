package com.tomaszwnuk.opencalendar.note

import com.tomaszwnuk.opencalendar.domain.record.RecordDto
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.DESCRIPTION_MAXIMUM_LENGTH
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.TITLE_MAXIMUM_LENGTH
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.*

data class NoteDto(

    override val id: UUID? = null,

    @field:Size(
        max = TITLE_MAXIMUM_LENGTH,
        message = "Title cannot be longer than $TITLE_MAXIMUM_LENGTH characters."
    )
    override val title: String? = null,

    @field:NotBlank(message = "Description cannot be blank.")
    @field:Size(
        max = DESCRIPTION_MAXIMUM_LENGTH,
        message = "Description cannot be longer than $DESCRIPTION_MAXIMUM_LENGTH characters."
    )
    override val description: String,

    @field:NotNull(message = "Calendar ID is required.")
    override val calendarId: UUID,

    override val categoryId: UUID? = null

) : RecordDto(id, title, description, categoryId)
