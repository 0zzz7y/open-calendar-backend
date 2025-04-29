package com.tomaszwnuk.opencalendar.domain.record

import com.tomaszwnuk.opencalendar.domain.entity.EntityDto
import java.util.*

open class RecordDto(

    id: UUID? = null,

    open val title: String? = null,

    open val description: String? = null,

    open val calendarId: UUID? = null,

    open val categoryId: UUID? = null

) : EntityDto(id = id)
