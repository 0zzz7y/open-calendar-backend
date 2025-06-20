package com.ozzz7y.opencalendar.domain.entity

import java.io.Serializable
import java.util.*

/**
 * The base entity data transfer object.
 */
abstract class EntityDto(

    /**
     * The unique identifier of the entity.
     */
    open val id: UUID? = null

) : Serializable
