package com.tomaszwnuk.opencalendar.domain.note

import java.util.*

data class NoteFilterDto(

    val name: String? = null,

    val description: String? = null,

    val calendarId: UUID? = null,

    val categoryId: UUID? = null

)
