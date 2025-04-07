package com.tomaszwnuk.dailyassistant.domain.entry

import com.tomaszwnuk.dailyassistant.domain.entity.EntityDto
import java.util.*

open class EntryDto(

    id: UUID? = null,

    open val name: String? = null,

    open val description: String? = null,

    open val categoryId: UUID? = null

) : EntityDto(id = id)
