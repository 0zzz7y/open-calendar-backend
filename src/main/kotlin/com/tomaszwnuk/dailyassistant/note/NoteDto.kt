package com.tomaszwnuk.dailyassistant.note

import com.tomaszwnuk.dailyassistant.domain.entry.EntryDto
import com.tomaszwnuk.dailyassistant.validation.FieldConstraints.DESCRIPTION_MAXIMUM_LENGTH
import com.tomaszwnuk.dailyassistant.validation.FieldConstraints.NAME_MAXIMUM_LENGTH
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

data class NoteDto(

    override val id: UUID? = null,

    @field:Size(
        max = NAME_MAXIMUM_LENGTH,
        message = "Description cannot be longer than $NAME_MAXIMUM_LENGTH characters."
    )
    override val name: String? = null,

    @field:NotBlank(message = "Description cannot be blank.")
    @field:Size(
        max = DESCRIPTION_MAXIMUM_LENGTH,
        message = "Description cannot be longer than $DESCRIPTION_MAXIMUM_LENGTH characters."
    )
    override val description: String,

    override val categoryId: UUID? = null

) : EntryDto(id, name, description, categoryId)
