package com.tomaszwnuk.dailyassistant.event

import com.tomaszwnuk.dailyassistant.domain.DomainEntity
import com.tomaszwnuk.dailyassistant.domain.RecurringPattern
import com.tomaszwnuk.dailyassistant.domain.Schedulable
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "event")
data class Event(

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    override val title: String,

    @Column(columnDefinition = "TEXT", nullable = true)
    override val description: String? = null,

    @Column(columnDefinition = "Date", nullable = false)
    override val date: LocalDateTime,

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "recurring_pattern", nullable = false)
    override val recurringPattern: RecurringPattern = RecurringPattern.NONE,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    val calendar: Calendar,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    val category: Category? = null
) : DomainEntity(), Schedulable {

    fun toDto(): EventDto {
        return EventDto(
            id = id,
            title = title,
            description = description,
            date = date,
            recurringPattern = recurringPattern,
            calendarId = calendar.id,
            categoryId = category?.id
        )
    }
}
