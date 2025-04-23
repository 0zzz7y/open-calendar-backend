package com.tomaszwnuk.opencalendar.domain.entry

import com.tomaszwnuk.opencalendar.domain.entity.EntityDto
import java.util.*

open class EntryDto(

    id: UUID? = null,

    open val name: String? = null,

    open val description: String? = null,

    open val calendarId: UUID? = null,

    open val categoryId: UUID? = null

) : EntityDto(id = id)
