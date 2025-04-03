package com.tomaszwnuk.dailyassistant.task

import com.tomaszwnuk.dailyassistant.domain.RecurringPattern
import java.time.LocalDateTime
import java.util.*

data class TaskDto(

    val id: UUID? = null,

    val title: String,

    val description: String,

    val date: LocalDateTime,

    val recurringPattern: RecurringPattern = RecurringPattern.NONE,

    val status: TaskStatus = TaskStatus.TODO,

    val calendarId: UUID? = null,

    val categoryId: UUID? = null
)
