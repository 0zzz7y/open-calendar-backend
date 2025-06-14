package com.tomaszwnuk.opencalendar.domain.note

import java.util.*

/**
 * The note filter data transfer object.
 */
data class NoteFilterDto(

    /**
     * The name of the note to filter by.
     */
    val name: String? = null,

    /**
     * The description of the note to filter by.
     */
    val description: String? = null,

    /**
     * The unique identifier of the calendar to which the note belongs.
     */
    val calendarId: UUID? = null,

    /**
     * The unique identifier of the category associated with the note.
     */
    val categoryId: UUID? = null

)
