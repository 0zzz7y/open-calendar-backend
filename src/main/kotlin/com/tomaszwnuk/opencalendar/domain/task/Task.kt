package com.tomaszwnuk.opencalendar.domain.task

import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_DESCRIPTION
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_ID
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_NAME
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_TASK_STATUS
import com.tomaszwnuk.opencalendar.domain.record.Record
import jakarta.persistence.*
import java.util.*

/**
 * The task entity.
 */
@Entity
@Table(name = "task")
data class Task(

    /**
     * The unique identifier of the task.
     */
    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    override val id: UUID = UUID.randomUUID(),

    /**
     * The name of the task.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_NAME, nullable = false)
    override val name: String,

    /**
     * The description of the task.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_DESCRIPTION, nullable = true)
    override val description: String? = null,

    /**
     * The status of the task.
     */
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = COLUMN_DEFINITION_TASK_STATUS, name = "status", nullable = false)
    val status: TaskStatus = TaskStatus.TODO,

    /**
     * The calendar to which the task belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    override val calendar: Calendar,

    /**
     * The category which the task is associated with.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    override val category: Category? = null

) : Record(
    id = id,
    name = name,
    description = description,
    calendar = calendar,
    category = category
) {

    /**
     * Converts the task entity to a data transfer object.
     *
     * @return The data transfer object representing the task.
     */
    override fun toDto(): TaskDto {
        return TaskDto(
            id = id,
            name = name,
            description = description,
            status = status,
            calendarId = calendar.id,
            categoryId = category?.id
        )
    }

}
