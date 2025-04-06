package com.tomaszwnuk.dailyassistant.calendar

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "calendar")
data class Calendar(

    @Id
    @Column(columnDefinition = "CHAR(36)", nullable = false)
    override val id: UUID = UUID.randomUUID(),

    @Column(columnDefinition = "VARCHAR(255)", unique = true, nullable = false)
    val name: String

) : com.tomaszwnuk.dailyassistant.domain.entity.Entity() {

    fun toDto(): CalendarDto {
        return CalendarDto(
            id = id,
            name = name
        )
    }

}
