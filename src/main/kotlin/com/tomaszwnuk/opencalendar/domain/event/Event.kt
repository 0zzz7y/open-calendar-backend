package com.tomaszwnuk.opencalendar.domain.event

import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_DATE
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_DESCRIPTION
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_ID
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_NAME
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_RECURRING_PATTERN
import com.tomaszwnuk.opencalendar.domain.record.Record
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

/**
 * The event entity.
 */
@Entity
@Table(name = "event")
data class Event(

    /**
     * The unique identifier of the event.
     */
    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    override val id: UUID = UUID.randomUUID(),

    /**
     * The name of the event.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_NAME, nullable = false)
    override val name: String,

    /**
     * The description of the event.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_DESCRIPTION, nullable = true)
    override val description: String? = null,

    /**
     * The start date and time of the event.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_DATE, nullable = false)
    override val startDate: LocalDateTime,

    /**
     * The end date and time of the event.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_DATE, nullable = false)
    override val endDate: LocalDateTime,

    /**
     * The recurring pattern of the event.
     */
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = COLUMN_DEFINITION_RECURRING_PATTERN, name = "recurring_pattern", nullable = false)
    override val recurringPattern: RecurringPattern = RecurringPattern.NONE,

    /**
     * The calendar to which the event belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    override val calendar: Calendar,

    /**
     * The category which the event is associated with.
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
), Schedulable {

    /**
     * Converts the event entity to a data transfer object.
     *
     * @return The data transfer object representing the event.
     */
    override fun toDto(): EventDto {
        return EventDto(
            id = id,
            name = name,
            description = description,
            startDate = startDate,
            endDate = endDate,
            recurringPattern = recurringPattern,
            calendarId = calendar.id,
            categoryId = category?.id
        )
    }

}
