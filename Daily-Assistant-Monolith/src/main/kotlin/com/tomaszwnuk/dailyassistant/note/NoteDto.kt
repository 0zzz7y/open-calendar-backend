package com.tomaszwnuk.dailyassistant.note

import com.tomaszwnuk.dailyassistant.domain.entry.EntryDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

data class NoteDto(

    override val id: UUID? = null,

    @field:NotBlank(message = "Name cannot be blank.")
    @field:Size(max = 255, message = "Name cannot be longer than 255 characters.")
    override val name: String,

    override val description: String? = null,

    override val categoryId: UUID? = null

) : EntryDto(id, name, description, categoryId)
