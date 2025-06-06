/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.record

import com.tomaszwnuk.opencalendar.domain.entity.EntityDto
import java.io.Serializable
import java.util.*

open class RecordDto(

    override val id: UUID? = null,

    open val name: String? = null,

    open val description: String? = null,

    open val calendarId: UUID? = null,

    open val categoryId: UUID? = null

) : EntityDto(id = id), Serializable
