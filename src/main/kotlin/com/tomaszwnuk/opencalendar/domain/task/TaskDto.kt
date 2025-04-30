/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.task

import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.DESCRIPTION_MAXIMUM_LENGTH
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.TITLE_MAXIMUM_LENGTH
import com.tomaszwnuk.opencalendar.domain.record.RecordDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.*

/**
 * Data Transfer Object (DTO) for representing a task.
 * Encapsulates the details of a task, including its title, description, status, and associations with a calendar and category.
 *
 * @property id The unique identifier of the task (optional).
 * @property title The title of the task. This field is mandatory and cannot be blank.
 * @property description The description of the task (optional). Limited to a maximum length defined by `DESCRIPTION_MAXIMUM_LENGTH`.
 * @property status The status of the task. This field is mandatory and defaults to `TaskStatus.TODO`.
 * @property calendarId The unique identifier of the associated calendar. This field is mandatory.
 * @property categoryId The unique identifier of the associated category (optional).
 */
data class TaskDto(

    /**
     * The unique identifier of the task.
     * This field is optional and can be null.
     */
    override val id: UUID? = null,

    /**
     * The title of the task.
     * This field is mandatory and cannot be blank.
     */
    @field:NotBlank(message = "Title cannot be blank.")
    @field:Size(
        max = TITLE_MAXIMUM_LENGTH,
        message = "Title cannot be longer than $TITLE_MAXIMUM_LENGTH characters."
    )
    override val title: String,

    /**
     * The description of the task.
     * This field is optional and limited to a maximum length defined by `DESCRIPTION_MAXIMUM_LENGTH`.
     */
    @field:Size(
        max = DESCRIPTION_MAXIMUM_LENGTH,
        message = "Description cannot be longer than $DESCRIPTION_MAXIMUM_LENGTH characters."
    )
    override val description: String? = null,

    /**
     * The status of the task.
     * This field is mandatory and defaults to `TaskStatus.TODO`.
     */
    @field:NotNull(message = "Task status is required.")
    val status: TaskStatus = TaskStatus.TODO,

    /**
     * The unique identifier of the associated calendar.
     * This field is mandatory and cannot be null.
     */
    @field:NotNull(message = "Calendar ID is required.")
    override val calendarId: UUID,

    /**
     * The unique identifier of the associated category.
     * This field is optional and can be null.
     */
    override val categoryId: UUID? = null

) : RecordDto(id, title, description, categoryId)
