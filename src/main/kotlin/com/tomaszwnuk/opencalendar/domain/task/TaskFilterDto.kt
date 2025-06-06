/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.task

import java.util.*

data class TaskFilterDto(

    val name: String? = null,

    val description: String? = null,

    val status: TaskStatus? = null,

    val calendarId: UUID? = null,

    val categoryId: UUID? = null

)
