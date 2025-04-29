package com.tomaszwnuk.opencalendar.note

import java.util.*

data class NoteFilterDto(

    val title: String? = null,

    val description: String? = null,

    val calendarId: UUID? = null,

    val categoryId: UUID? = null

)
