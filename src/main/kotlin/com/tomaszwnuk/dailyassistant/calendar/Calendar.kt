package com.tomaszwnuk.dailyassistant.calendar

import com.tomaszwnuk.dailyassistant.validation.FieldConstraints.COLUMN_DEFINITION_ID
import com.tomaszwnuk.dailyassistant.validation.FieldConstraints.COLUMN_DEFINITION_NAME
import com.tomaszwnuk.dailyassistant.validation.FieldConstraints.COLUMN_DEFINITION_EMOJI
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

) : com.tomaszwnuk.dailyassistant.domain.entity.Entity() {

    fun toDto(): CalendarDto {
        return CalendarDto(
            id = id,
            name = name,
            emoji = emoji
        )
    }

}
