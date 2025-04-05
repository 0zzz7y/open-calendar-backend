package com.tomaszwnuk.dailyassistant.note

import com.tomaszwnuk.dailyassistant.domain.entry.EntryDto
import java.util.*

data class NoteDto(

    override val id: UUID? = null,

    override val name: String,

    override val description: String? = null,

    override val categoryId: UUID? = null

) : EntryDto(id, name, description, categoryId)
