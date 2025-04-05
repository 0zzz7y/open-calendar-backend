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

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    override val name: String? = null,

    @Column(columnDefinition = "TEXT", nullable = false)
    override val description: String,

    @Column(columnDefinition = "Date", nullable = false)
    override val date: LocalDateTime,

    @Column(name = "recurring_pattern", nullable = false)
    override val recurringPattern: RecurringPattern = RecurringPattern.NONE,

    @Enumerated(EnumType.ORDINAL)
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
            date = date,
            recurringPattern = recurringPattern,
            status = status,
            calendarId = calendar?.id,
            categoryId = category?.id
        )
    }

}
