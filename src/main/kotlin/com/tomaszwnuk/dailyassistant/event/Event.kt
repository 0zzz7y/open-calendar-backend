package com.tomaszwnuk.dailyassistant.event

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
@Table(name = "event")
data class Event(

    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    override val id: UUID = UUID.randomUUID(),

    @Column(columnDefinition = COLUMN_DEFINITION_NAME, nullable = false)
    override val name: String,

    @Column(columnDefinition = COLUMN_DEFINITION_DESCRIPTION, nullable = true)
    override val description: String? = null,

    @Column(columnDefinition = COLUMN_DEFINITION_DATE, nullable = false)
    override val startDate: LocalDateTime,

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

) : Entry(
    id = id,
    name = name,
    description = description,
    calendar = calendar,
    category = category
), Schedulable {

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
