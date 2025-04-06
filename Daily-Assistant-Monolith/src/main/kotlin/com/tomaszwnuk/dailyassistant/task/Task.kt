package com.tomaszwnuk.dailyassistant.task

import com.tomaszwnuk.dailyassistant.calendar.Calendar
import com.tomaszwnuk.dailyassistant.category.Category
import com.tomaszwnuk.dailyassistant.domain.RecurringPattern
import com.tomaszwnuk.dailyassistant.domain.Schedulable
import com.tomaszwnuk.dailyassistant.domain.entry.Entry
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "task")
data class Task(

    @Column(columnDefinition = "VARCHAR(255)", nullable = true)
    override val name: String? = null,

    @Column(columnDefinition = "VARCHAR(4096)", nullable = false)
    override val description: String,

    @Column(columnDefinition = "TIMESTAMP", nullable = true)
    override val startDate: LocalDateTime? = null,

    @Column(columnDefinition = "TIMESTAMP", nullable = true)
    override val endDate: LocalDateTime? = null,

    @Column(name = "recurring_pattern", nullable = false)
    override val recurringPattern: RecurringPattern = RecurringPattern.NONE,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: TaskStatus = TaskStatus.TODO,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = true)
    val calendar: Calendar? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    override val category: Category? = null

) : Entry(name, description, category), Schedulable {

    override fun toDto(): TaskDto {
        return TaskDto(
            id = id,
            name = name,
            description = description,
            startDate = startDate,
            endDate = endDate,
            recurringPattern = recurringPattern.value,
            status = status.value,
            calendarId = calendar?.id,
            categoryId = category?.id
        )
    }

}
