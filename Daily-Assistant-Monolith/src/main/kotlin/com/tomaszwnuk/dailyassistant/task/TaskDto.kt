package com.tomaszwnuk.dailyassistant.task

import com.tomaszwnuk.dailyassistant.domain.RecurringPattern
import com.tomaszwnuk.dailyassistant.domain.entry.EntryDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.*

data class TaskDto(

    override val id: UUID? = null,

    @field:Size(max = 255, message = "Name cannot be longer than 255 characters.")
    override val name: String? = null,

    @field:NotBlank(message = "Description cannot be blank.")
    @field:Size(max = 4096, message = "Description cannot be longer than 4096 characters.")
    override val description: String,

    val startDate: LocalDateTime? = null,

    val endDate: LocalDateTime? = null,

    val recurringPattern: String = RecurringPattern.NONE.value,

    val status: String = TaskStatus.TODO.value,

    val calendarId: UUID? = null,

    override val categoryId: UUID? = null

) : EntryDto(id, name, description, categoryId)
