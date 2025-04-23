package com.tomaszwnuk.opencalendar.task

import com.tomaszwnuk.opencalendar.calendar.Calendar
import com.tomaszwnuk.opencalendar.category.Category
import com.tomaszwnuk.opencalendar.domain.RecurringPattern
import com.tomaszwnuk.opencalendar.domain.Schedulable
import com.tomaszwnuk.opencalendar.domain.entry.Entry
import com.tomaszwnuk.opencalendar.validation.FieldConstraints.COLUMN_DEFINITION_DATE
import com.tomaszwnuk.opencalendar.validation.FieldConstraints.COLUMN_DEFINITION_DESCRIPTION
import com.tomaszwnuk.opencalendar.validation.FieldConstraints.COLUMN_DEFINITION_ID
import com.tomaszwnuk.opencalendar.validation.FieldConstraints.COLUMN_DEFINITION_NAME
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
    @JoinColumn(name = "calendar_id", nullable = false)
    override val calendar: Calendar,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    override val category: Category? = null

) : Entry(
    id = id,
    name = name,
    description = description,
    calendar = calendar,
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
            calendarId = calendar.id,
            categoryId = category?.id
        )
    }

}
