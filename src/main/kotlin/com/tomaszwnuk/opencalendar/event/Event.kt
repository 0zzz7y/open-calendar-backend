package com.tomaszwnuk.opencalendar.event

import com.tomaszwnuk.opencalendar.calendar.Calendar
import com.tomaszwnuk.opencalendar.category.Category
import com.tomaszwnuk.opencalendar.domain.RecurringPattern
import com.tomaszwnuk.opencalendar.domain.Schedulable
import com.tomaszwnuk.opencalendar.domain.date.LocalDateTimeConverter
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_DATE
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_DESCRIPTION
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_ID
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_TITLE
import com.tomaszwnuk.opencalendar.domain.record.Record
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "event")
data class Event(

    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    override val id: UUID = UUID.randomUUID(),

    @Column(columnDefinition = COLUMN_DEFINITION_TITLE, nullable = false)
    override val title: String,

    @Column(columnDefinition = COLUMN_DEFINITION_DESCRIPTION, nullable = true)
    override val description: String? = null,

    @Convert(converter = LocalDateTimeConverter::class)
    @Column(columnDefinition = COLUMN_DEFINITION_DATE, nullable = false)
    override val startDate: LocalDateTime,

    @Convert(converter = LocalDateTimeConverter::class)
    @Column(columnDefinition = COLUMN_DEFINITION_DATE, nullable = false)
    override val endDate: LocalDateTime,

    @Enumerated(EnumType.STRING)
    @Column(name = "recurring_pattern", nullable = false)
    override val recurringPattern: RecurringPattern = RecurringPattern.NONE,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    override val calendar: Calendar,

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
