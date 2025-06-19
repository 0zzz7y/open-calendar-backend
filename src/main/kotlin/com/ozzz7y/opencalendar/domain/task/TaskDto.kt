package com.ozzz7y.opencalendar.domain.task

import com.ozzz7y.opencalendar.domain.field.FieldConstraints.DESCRIPTION_MAXIMUM_LENGTH
import com.ozzz7y.opencalendar.domain.field.FieldConstraints.NAME_MAXIMUM_LENGTH
import com.ozzz7y.opencalendar.domain.record.RecordDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.*

/**
 * The task data transfer object.
 */
data class TaskDto(

    /**
     * The unique identifier of the task.
     */
    override val id: UUID? = null,

    /**
     * The name of the task.
     */
    @field:NotBlank(message = "Name cannot be blank.")
    @field:Size(
        max = NAME_MAXIMUM_LENGTH,
        message = "Name cannot be longer than $NAME_MAXIMUM_LENGTH characters."
    )
    override val name: String,

    /**
     * The description of the task.
     */
    @field:Size(
        max = DESCRIPTION_MAXIMUM_LENGTH,
        message = "Description cannot be longer than $DESCRIPTION_MAXIMUM_LENGTH characters."
    )
    override val description: String? = null,

    /**
     * The status of the task.
     */
    @field:NotNull(message = "Task status is required.")
    val status: TaskStatus = TaskStatus.TODO,

    /**
     * The unique identifier of the calendar to which the task belongs.
     */
    @field:NotNull(message = "Calendar ID is required.")
    override val calendarId: UUID,

    /**
     * The unique identifier of the category associated with the task.
     */
    override val categoryId: UUID? = null

) : RecordDto(id, name, description, categoryId)
