package com.ozzz7y.opencalendar.domain.record

import com.ozzz7y.opencalendar.domain.calendar.Calendar
import com.ozzz7y.opencalendar.domain.category.Category
import com.ozzz7y.opencalendar.domain.entity.Entity
import com.ozzz7y.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_DESCRIPTION
import com.ozzz7y.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_ID
import com.ozzz7y.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_NAME
import jakarta.persistence.*
import java.util.*

/**
 * The record entity.
 */
@MappedSuperclass
abstract class Record(

    /**
     * The unique identifier of the record.
     */
    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    override val id: UUID = UUID.randomUUID(),

    /**
     * The name of the record.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_NAME, nullable = true)
    val name: String? = null,

    /**
     * The description of the record.
     */
    @Column(columnDefinition = COLUMN_DEFINITION_DESCRIPTION, nullable = true)
    val description: String? = null,

    /**
     * The calendar to which the record belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    val calendar: Calendar,

    /**
     * The category which the record is associated with.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    val category: Category? = null

) : Entity(id = id) {

    /**
     * Converts the record entity to a data transfer object.
     *
     * @return The data transfer object representing the record.
     */
    fun toDto(): RecordDto {
        return RecordDto(
            id = id,
            name = name,
            description = description,
            calendarId = calendar.id,
            categoryId = category?.id
        )
    }

}
