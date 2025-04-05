package com.tomaszwnuk.dailyassistant.task

import com.tomaszwnuk.dailyassistant.domain.RecurringPattern
import com.tomaszwnuk.dailyassistant.domain.entry.EntryDto
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime
import java.util.*

data class TaskDto(

    override val id: UUID? = null,

    @field:NotBlank(message = "Name cannot be blank")
    override val name: String? = null,

    override val description: String,

    val date: LocalDateTime,

    val recurringPattern: RecurringPattern = RecurringPattern.NONE,

    val status: TaskStatus = TaskStatus.TODO,

    val calendarId: UUID? = null,

    override val categoryId: UUID? = null

) : EntryDto(id, name, description, categoryId)
