package com.ozzz7y.opencalendar.domain.task

import java.util.*

/**
 * The task filter data transfer object.
 */
data class TaskFilterDto(

    /**
     * The name of the task to filter by.
     */
    val name: String? = null,

    /**
     * The description of the task to filter by.
     */
    val description: String? = null,

    /**
     * The status of the task to filter by.
     */
    val status: TaskStatus? = null,

    /**
     * The unique identifier of the calendar to which the task belongs.
     */
    val calendarId: UUID? = null,

    /**
     * The unique identifier of the category associated with the task.
     */
    val categoryId: UUID? = null

)
