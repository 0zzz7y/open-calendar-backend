package com.tomaszwnuk.opencalendar.domain.calendar

import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_EMOJI
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_ID
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_NAME
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "calendar")
data class Calendar(

    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    override val id: UUID = UUID.randomUUID(),

    @Column(columnDefinition = COLUMN_DEFINITION_NAME, unique = true, nullable = false)
    val name: String,

    @Column(columnDefinition = COLUMN_DEFINITION_EMOJI, unique = false, nullable = false)
    val emoji: String

) : com.tomaszwnuk.opencalendar.domain.entity.Entity() {

    fun toDto(): CalendarDto {
        return CalendarDto(
            id = id,
            name = name,
            emoji = emoji
        )
    }

}
