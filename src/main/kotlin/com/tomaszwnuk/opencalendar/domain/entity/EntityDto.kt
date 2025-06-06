/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.entity

import java.io.Serializable
import java.util.*

abstract class EntityDto(

    open val id: UUID? = null

) : Serializable
