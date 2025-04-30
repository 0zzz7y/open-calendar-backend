/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.record

import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.entity.Entity
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_DESCRIPTION
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_ID
import jakarta.persistence.*
import java.util.*

/**
 * Abstract base class representing a record entity.
 * Provides common properties and functionality for records, such as title, description, and associations with a calendar and category.
 *
 * @property id The unique identifier of the record.
 * @property title The title of the record (optional).
 * @property description The description of the record (optional).
 * @property calendar The calendar associated with the record (mandatory).
 * @property category The category associated with the record (optional).
 */
@MappedSuperclass
abstract class Record(

    /**
     * The unique identifier of the record.
     * This field is mandatory, not updatable, and uses a UUID.
     */
    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    override val id: UUID = UUID.randomUUID(),

    /**
     * The title of the record.
     * This field is optional and can be null.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_DESCRIPTION, nullable = true)
    val title: String? = null,

    /**
     * The description of the record.
     * This field is optional and can be null.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_DESCRIPTION, nullable = true)
    val description: String? = null,

    /**
     * The calendar associated with the record.
     * This field is mandatory and cannot be null.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    val calendar: Calendar,

    /**
     * The category associated with the record.
     * This field is optional and can be null.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    val category: Category? = null

) : Entity(id = id) {

    /**
     * Converts the record entity to a data transfer object (DTO).
     *
     * @return A `RecordDto` containing the record's details.
     */
    fun toDto(): RecordDto {
        return RecordDto(
            id = id,
            title = title,
            description = description,
            calendarId = calendar.id,
            categoryId = category?.id
        )
    }

}
