/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.mapper

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

/**
 * Utility object for mapping lists to paginated Page objects.
 */
object PageMapper {

    /**
     * Converts a list to a paginated Page object based on the provided Pageable.
     *
     * @receiver The list to be converted to a Page.
     * @param pageable The Pageable object containing pagination information (page number, size, etc.).
     * @return A Page object containing the paginated content.
     */
    fun <T> List<T>.toPage(pageable: Pageable): Page<T> {
        val start: Int = pageable.offset.toInt()
        val end: Int = (start + pageable.pageSize).coerceAtMost(this.size)
        val content: List<T> = if (start <= end) this.subList(start, end) else emptyList()
        return PageImpl(content, pageable, this.size.toLong())
    }

}
