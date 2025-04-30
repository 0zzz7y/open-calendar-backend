/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.task

import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_DESCRIPTION
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_ID
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_TITLE
import com.tomaszwnuk.opencalendar.domain.record.Record
import jakarta.persistence.*
import java.util.*

/**
 * Entity class representing a task.
 * Extends the `Record` class and includes additional properties specific to tasks, such as status.
 *
 * @property id The unique identifier of the task. This field is mandatory, not updatable, and uses a UUID.
 * @property title The title of the task. This field is mandatory and cannot be null.
 * @property description The description of the task. This field is optional and can be null.
 * @property status The status of the task, represented as an enumerated value. Defaults to `TaskStatus.TODO`.
 * @property calendar The calendar associated with the task. This field is mandatory and cannot be null.
 * @property category The category associated with the task. This field is optional and can be null.
 */
@Entity
@Table(name = "task")
data class Task(

    /**
     * The unique identifier of the task.
     * This field is mandatory, not updatable, and uses a UUID.
     */
    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    override val id: UUID = UUID.randomUUID(),

    /**
     * The title of the task.
     * This field is mandatory and cannot be null.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_TITLE, nullable = false)
    override val title: String,

    /**
     * The description of the task.
     * This field is optional and can be null.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_DESCRIPTION, nullable = true)
    override val description: String? = null,

    /**
     * The status of the task, represented as an enumerated value.
     * Defaults to `TaskStatus.TODO`.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: TaskStatus = TaskStatus.TODO,

    /**
     * The calendar associated with the task.
     * This field is mandatory and cannot be null.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    override val calendar: Calendar,

    /**
     * The category associated with the task.
     * This field is optional and can be null.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    override val category: Category? = null

) : Record(
    id = id,
    title = title,
    description = description,
    calendar = calendar,
    category = category
) {

    /**
     * Converts the task entity to a data transfer object (DTO).
     *
     * @return A `TaskDto` containing the task's details.
     */
    override fun toDto(): TaskDto {
        return TaskDto(
            id = id,
            title = title,
            description = description,
            status = status,
            calendarId = calendar.id,
            categoryId = category?.id
        )
    }

}
