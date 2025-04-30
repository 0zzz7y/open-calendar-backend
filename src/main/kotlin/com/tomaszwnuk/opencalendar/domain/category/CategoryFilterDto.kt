/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.category

/**
 * Data Transfer Object (DTO) for filtering categories.
 * Used to specify criteria for filtering categories in the system.
 *
 * @property title The title to filter categories by. Optional.
 * @property color The color to filter categories by. Optional.
 */
data class CategoryFilterDto(

    /**
     * The title to filter categories by.
     * This field is optional and can be null.
     */
    val title: String? = null,

    /**
     * The color to filter categories by.
     * This field is optional and can be null.
     */
    val color: String? = null

)
