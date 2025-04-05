package com.tomaszwnuk.dailyassistant.task

import com.tomaszwnuk.dailyassistant.domain.RecurringPattern
import com.tomaszwnuk.dailyassistant.domain.entry.EntryDto
import jakarta.validation.constraints.*
import java.time.LocalDateTime
import java.util.*

data class TaskDto(

    override val id: UUID? = null,

    @field:NotBlank(message = "Name cannot be blank.")
    @field:Size(max = 255, message = "Name cannot be longer than 255 characters.")
    override val name: String? = null,

    @field:NotBlank(message = "Description cannot be blank.")
    override val description: String,

    @field:NotNull(message = "Date is required.")
    val date: LocalDateTime,

    @field:Min(0, message = "Invalid recurring pattern.")
    @field:Max(4, message = "Invalid recurring pattern.")
    val recurringPattern: RecurringPattern = RecurringPattern.NONE,

    @field:Min(0, message = "Invalid task status.")
    @field:Max(2, message = "Invalid task status.")
    val status: TaskStatus = TaskStatus.TODO,

    @field:NotNull(message = "Calendar ID is required.")
    val calendarId: UUID? = null,

    override val categoryId: UUID? = null

) : EntryDto(id, name, description, categoryId)
