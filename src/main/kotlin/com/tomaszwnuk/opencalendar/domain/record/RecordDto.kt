/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.record

import com.tomaszwnuk.opencalendar.domain.entity.EntityDto
import java.io.Serializable
import java.util.*

/**
 * Data Transfer Object (DTO) for representing a record.
 * Encapsulates the details of a record, including its title, description, and associations with a calendar and category.
 *
 * @property id The unique identifier of the record (optional).
 * @property title The title of the record (optional).
 * @property description The description of the record (optional).
 * @property calendarId The unique identifier of the associated calendar (optional).
 * @property categoryId The unique identifier of the associated category (optional).
 */
open class RecordDto(

    /**
     * The unique identifier of the record.
     * This field is optional and can be null.
     */
    override val id: UUID? = null,

    /**
     * The title of the record.
     * This field is optional and can be null.
     */
    open val title: String? = null,

    /**
     * The description of the record.
     * This field is optional and can be null.
     */
    open val description: String? = null,

    /**
     * The unique identifier of the associated calendar.
     * This field is optional and can be null.
     */
    open val calendarId: UUID? = null,

    /**
     * The unique identifier of the associated category.
     * This field is optional and can be null.
     */
    open val categoryId: UUID? = null

) : EntityDto(id = id), Serializable
