package com.tomaszwnuk.opencalendar.task

import com.tomaszwnuk.opencalendar.domain.RecurringPattern
import com.tomaszwnuk.opencalendar.domain.Schedulable
import com.tomaszwnuk.opencalendar.domain.entry.EntryDto
import com.tomaszwnuk.opencalendar.validation.FieldConstraints.DESCRIPTION_MAXIMUM_LENGTH
import com.tomaszwnuk.opencalendar.validation.FieldConstraints.NAME_MAXIMUM_LENGTH
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
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

    override val startDate: LocalDateTime? = null,

    override val endDate: LocalDateTime? = null,

    @field:NotNull(message = "Recurring pattern is required.")
    override val recurringPattern: RecurringPattern = RecurringPattern.NONE,

    @field:NotNull(message = "Task status is required.")
    val status: TaskStatus = TaskStatus.TODO,

    @field:NotNull(message = "Calendar ID is required.")
    override val calendarId: UUID,

    override val categoryId: UUID? = null

) : EntryDto(id, name, description, categoryId), Schedulable
