package com.tomaszwnuk.opencalendar.domain.calendar

import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_EMOJI
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_ID
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_NAME
import jakarta.persistence.*
import java.util.*

/**
 * The calendar entity.
 */
@Entity
@Table(name = "calendar")
data class Calendar(

    /**
     * The unique identifier of the calendar.
     */
    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    override val id: UUID = UUID.randomUUID(),

    /**
     * The name of the calendar.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_NAME, unique = true, nullable = false)
    val name: String,

    /**
     * The emoji representing the calendar.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_EMOJI, unique = false, nullable = false)
    val emoji: String,

    /**
     * The unique identifier of the user who owns the calendar.
     */
    @JoinColumn(name = "user_id", unique = false, nullable = false)
    val userId: UUID

) : com.tomaszwnuk.opencalendar.domain.entity.Entity() {

    /**
     * Converts the calendar entity to a data transfer object.
     *
     * @return The data transfer object representing the calendar.
     */
    fun toDto(): CalendarDto {
        return CalendarDto(
            id = id,
            name = name,
            emoji = emoji
        )
    }

}
