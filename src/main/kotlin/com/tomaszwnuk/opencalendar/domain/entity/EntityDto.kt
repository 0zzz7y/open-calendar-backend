/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.entity

import java.io.Serializable
import java.util.*

/**
 * Abstract base class for Data Transfer Objects (DTOs) in the application.
 * Provides a common structure for DTOs with an optional unique identifier.
 *
 * @property id The unique identifier for the DTO. Defaults to null.
 */
abstract class EntityDto(

    /**
     * The unique identifier for the DTO.
     * Can be null if the DTO is not yet associated with a specific entity.
     */
    open val id: UUID? = null

) : Serializable
