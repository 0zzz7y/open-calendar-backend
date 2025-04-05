package com.tomaszwnuk.dailyassistant.event

import com.tomaszwnuk.dailyassistant.calendar.Calendar
import com.tomaszwnuk.dailyassistant.category.Category
import com.tomaszwnuk.dailyassistant.domain.RecurringPattern
import com.tomaszwnuk.dailyassistant.domain.Schedulable
import com.tomaszwnuk.dailyassistant.domain.entry.Entry
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "event")
data class Event(

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    override val name: String,

    @Column(columnDefinition = "TEXT", nullable = true)
    override val description: String? = null,

    @Column(columnDefinition = "Date", nullable = false)
    override val date: LocalDateTime,

    @Enumerated(EnumType.STRING)
    @Column(name = "recurring_pattern", nullable = false)
    override val recurringPattern: RecurringPattern = RecurringPattern.NONE,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    val calendar: Calendar,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    override val category: Category? = null

) : Entry(name, description, category), Schedulable {

    override fun toDto(): EventDto {
        return EventDto(
            id = id,
            name = name,
            description = description,
            date = date,
            recurringPattern = recurringPattern,
            calendarId = calendar.id,
            categoryId = category?.id
        )
    }

}
