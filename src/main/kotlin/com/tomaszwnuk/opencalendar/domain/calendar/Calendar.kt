/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.calendar

import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_EMOJI
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_ID
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_TITLE
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

/**
 * Entity class representing a calendar.
 * Maps to the `calendar` table in the database.
 *
 * @property id The unique identifier for the calendar.
 * @property title The title of the calendar.
 * @property emoji The emoji associated with the calendar.
 */
@Entity
@Table(name = "calendar")
data class Calendar(

    /**
     * The unique identifier for the calendar.
     * This field is non-nullable and cannot be updated.
     */
    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    override val id: UUID = UUID.randomUUID(),

    /**
     * The title of the calendar.
     * This field is unique and non-nullable.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_TITLE, unique = true, nullable = false)
    val title: String,

    /**
     * The emoji associated with the calendar.
     * This field is non-nullable but not unique.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_EMOJI, unique = false, nullable = false)
    val emoji: String

) : com.tomaszwnuk.opencalendar.domain.entity.Entity() {

    /**
     * Converts the `Calendar` entity to a `CalendarDto`.
     *
     * @return A `CalendarDto` containing the calendar's data.
     */
    fun toDto(): CalendarDto {
        return CalendarDto(
            id = id,
            title = title,
            emoji = emoji
        )
    }

}
