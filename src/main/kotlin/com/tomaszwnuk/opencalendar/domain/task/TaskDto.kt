package com.tomaszwnuk.opencalendar.domain.task

import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.DESCRIPTION_MAXIMUM_LENGTH
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.NAME_MAXIMUM_LENGTH
import com.tomaszwnuk.opencalendar.domain.record.RecordDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.*

data class TaskDto(

    override val id: UUID? = null,

    @field:NotBlank(message = "Name cannot be blank.")
    @field:Size(
        max = NAME_MAXIMUM_LENGTH,
        message = "Name cannot be longer than $NAME_MAXIMUM_LENGTH characters."
    )
    override val name: String,

    @field:Size(
        max = DESCRIPTION_MAXIMUM_LENGTH,
        message = "Description cannot be longer than $DESCRIPTION_MAXIMUM_LENGTH characters."
    )
    override val description: String? = null,

    @field:NotNull(message = "Task status is required.")
    val status: TaskStatus = TaskStatus.TODO,

    @field:NotNull(message = "Calendar ID is required.")
    override val calendarId: UUID,

    override val categoryId: UUID? = null

) : RecordDto(id, name, description, categoryId)
