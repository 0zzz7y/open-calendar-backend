/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.note

import java.util.*

/**
 * Data Transfer Object (DTO) for filtering notes.
 * Represents the criteria used to filter notes in the application.
 *
 * @property title The title of the note to filter by (optional).
 * @property description The description of the note to filter by (optional).
 * @property calendarId The unique identifier of the calendar to filter by (optional).
 * @property categoryId The unique identifier of the category to filter by (optional).
 */
data class NoteFilterDto(

    /**
     * The title of the note to filter by.
     * This field is optional and can be null.
     */
    val title: String? = null,

    /**
     * The description of the note to filter by.
     * This field is optional and can be null.
     */
    val description: String? = null,

    /**
     * The unique identifier of the calendar to filter by.
     * This field is optional and can be null.
     */
    val calendarId: UUID? = null,

    /**
     * The unique identifier of the category to filter by.
     * This field is optional and can be null.
     */
    val categoryId: UUID? = null

)
