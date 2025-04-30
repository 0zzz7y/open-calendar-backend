/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.task

import java.util.*

/**
 * Data Transfer Object (DTO) for filtering tasks.
 * Encapsulates the criteria used to filter tasks, such as title, description, status, and associations with a calendar or category.
 *
 * @property title The title of the task to filter by (optional).
 * @property description The description of the task to filter by (optional).
 * @property status The status of the task to filter by (optional).
 * @property calendarId The unique identifier of the associated calendar to filter by (optional).
 * @property categoryId The unique identifier of the associated category to filter by (optional).
 */
data class TaskFilterDto(

    /**
     * The title of the task to filter by.
     * This field is optional and can be null.
     */
    val title: String? = null,

    /**
     * The description of the task to filter by.
     * This field is optional and can be null.
     */
    val description: String? = null,

    /**
     * The status of the task to filter by.
     * This field is optional and can be null.
     */
    val status: TaskStatus? = null,

    /**
     * The unique identifier of the associated calendar to filter by.
     * This field is optional and can be null.
     */
    val calendarId: UUID? = null,

    /**
     * The unique identifier of the associated category to filter by.
     * This field is optional and can be null.
     */
    val categoryId: UUID? = null

)
