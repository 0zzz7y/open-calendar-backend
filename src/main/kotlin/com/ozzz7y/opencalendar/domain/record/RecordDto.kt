package com.ozzz7y.opencalendar.domain.record

import com.ozzz7y.opencalendar.domain.entity.EntityDto
import java.io.Serializable
import java.util.*

/**
 * The record data transfer object.
 */
open class RecordDto(

    /**
     * The unique identifier of the record.
     */
    override val id: UUID? = null,

    /**
     * The name of the record.
     */
    open val name: String? = null,

    /**
     * The description of the record.
     */
    open val description: String? = null,

    /**
     * The unique identifier of the calendar to which the record belongs.
     */
    open val calendarId: UUID? = null,

    /**
     * The unique identifier of the category associated with the record.
     */
    open val categoryId: UUID? = null

) : EntityDto(id = id), Serializable
