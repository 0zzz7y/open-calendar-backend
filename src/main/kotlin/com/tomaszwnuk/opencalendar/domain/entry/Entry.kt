package com.tomaszwnuk.opencalendar.domain.entry

import com.tomaszwnuk.opencalendar.calendar.Calendar
import com.tomaszwnuk.opencalendar.category.Category
import com.tomaszwnuk.opencalendar.domain.entity.Entity
import com.tomaszwnuk.opencalendar.validation.FieldConstraints.COLUMN_DEFINITION_DESCRIPTION
import com.tomaszwnuk.opencalendar.validation.FieldConstraints.COLUMN_DEFINITION_ID
import jakarta.persistence.*
import java.util.*

@MappedSuperclass
abstract class Entry(

    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    override val id: UUID = UUID.randomUUID(),

    @Column(columnDefinition = COLUMN_DEFINITION_DESCRIPTION, nullable = true)
    val name: String? = null,

    @Column(columnDefinition = COLUMN_DEFINITION_DESCRIPTION, nullable = true)
    val description: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    val calendar: Calendar,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    val category: Category? = null

) : Entity(id = id) {

    fun toDto(): EntryDto {
        return EntryDto(
            id = id,
            name = name,
            description = description,
            calendarId = calendar.id,
            categoryId = category?.id
        )
    }

}
