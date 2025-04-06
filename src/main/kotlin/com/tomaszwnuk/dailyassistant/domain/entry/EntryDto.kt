package com.tomaszwnuk.dailyassistant.domain.entry

import java.util.*

open class EntryDto(

    open val id: UUID? = null,

    open val name: String? = null,

    open val description: String? = null,

    open val categoryId: UUID? = null

)
