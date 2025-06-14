package com.tomaszwnuk.opencalendar.domain.category

/**
 * The category filter data transfer object.
 */
data class CategoryFilterDto(

    /**
     * The name of the category to filter by.
     */
    val name: String? = null,

    /**
     * The color of the category to filter by.
     */
    val color: String? = null

)
