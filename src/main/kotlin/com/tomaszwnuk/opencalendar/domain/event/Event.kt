/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.event

import com.tomaszwnuk.opencalendar.common.date.LocalDateTimeConverter
import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_DATE
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_DESCRIPTION
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_ID
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_TITLE
import com.tomaszwnuk.opencalendar.domain.record.Record
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

/**
 * Entity class representing an event in the application.
 * Stores details about an event, including its title, description, start and end dates,
 * recurring pattern, associated calendar, and optional category.
 *
 * @property id The unique identifier for the event, generated as a UUID.
 * @property title The title of the event, cannot be null.
 * @property description An optional description of the event.
 * @property startDate The start date and time of the event.
 * @property endDate The end date and time of the event.
 * @property recurringPattern The recurring pattern of the event, defaults to NONE.
 * @property calendar The calendar to which the event belongs.
 * @property category An optional category associated with the event.
 */
@Entity
@Table(name = "event")
data class Event(

    /**
     * The unique identifier for the event.
     * Automatically generated as a UUID and cannot be updated.
     */
    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    override val id: UUID = UUID.randomUUID(),

    /**
     * The title of the event.
     * Cannot be null.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_TITLE, nullable = false)
    override val title: String,

    /**
     * An optional description of the event.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_DESCRIPTION, nullable = true)
    override val description: String? = null,

    /**
     * The start date and time of the event.
     */
    @Convert(converter = LocalDateTimeConverter::class)
    @Column(columnDefinition = COLUMN_DEFINITION_DATE, nullable = false)
    override val startDate: LocalDateTime,

    /**
     * The end date and time of the event.
     */
    @Convert(converter = LocalDateTimeConverter::class)
    @Column(columnDefinition = COLUMN_DEFINITION_DATE, nullable = false)
    override val endDate: LocalDateTime,

    /**
     * The recurring pattern of the event.
     * Defaults to NONE.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "recurring_pattern", nullable = false)
    override val recurringPattern: RecurringPattern = RecurringPattern.NONE,

    /**
     * The calendar to which the event belongs.
     * Cannot be null.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    override val calendar: Calendar,

    /**
     * An optional category associated with the event.
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
), Schedulable {

    /**
     * Converts the event entity to its corresponding Data Transfer Object (DTO).
     *
     * @return The event as a DTO.
     */
    override fun toDto(): EventDto {
        return EventDto(
            id = id,
            title = title,
            description = description,
            startDate = startDate,
            endDate = endDate,
            recurringPattern = recurringPattern,
            calendarId = calendar.id,
            categoryId = category?.id
        )
    }

}