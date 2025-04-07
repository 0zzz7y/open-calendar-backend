package com.tomaszwnuk.dailyassistant.task

import com.tomaszwnuk.dailyassistant.calendar.Calendar
import com.tomaszwnuk.dailyassistant.category.Category
import com.tomaszwnuk.dailyassistant.domain.RecurringPattern
import com.tomaszwnuk.dailyassistant.domain.Schedulable
import com.tomaszwnuk.dailyassistant.domain.entry.Entry
import com.tomaszwnuk.dailyassistant.validation.FieldConstraints.COLUMN_DEFINITION_DATE
import com.tomaszwnuk.dailyassistant.validation.FieldConstraints.COLUMN_DEFINITION_DESCRIPTION
import com.tomaszwnuk.dailyassistant.validation.FieldConstraints.COLUMN_DEFINITION_ID
import com.tomaszwnuk.dailyassistant.validation.FieldConstraints.COLUMN_DEFINITION_NAME
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "task")
data class Task(

    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    override val id: UUID = UUID.randomUUID(),

    @Column(columnDefinition = COLUMN_DEFINITION_NAME, nullable = false)
    override val name: String,

    @Column(columnDefinition = COLUMN_DEFINITION_DESCRIPTION, nullable = true)
    override val description: String? = null,

    @Column(columnDefinition = COLUMN_DEFINITION_DATE, nullable = true)
    override val startDate: LocalDateTime? = null,

    @Column(columnDefinition = COLUMN_DEFINITION_DATE, nullable = true)
    override val endDate: LocalDateTime? = null,

    @Enumerated(EnumType.STRING)
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

) : Entry(
    id = id,
    name = name,
    description = description,
    category = category
), Schedulable {

    override fun toDto(): TaskDto {
        return TaskDto(
            id = id,
            name = name,
            description = description,
            startDate = startDate,
            endDate = endDate,
            recurringPattern = recurringPattern,
            status = status,
            calendarId = calendar?.id,
            categoryId = category?.id
        )
    }

}
