package com.tomaszwnuk.opencalendar.domain.mapper

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

/**
 * The mapper for converting a list to a paginated page.
 */
object PageMapper {

    /**
     * Converts a list to a paginated page based on the provided pageable.
     *
     * @param pageable The pagination information
     *
     * @return A Page containing the sublist of the original list
     */
    fun <T> List<T>.toPage(pageable: Pageable): Page<T> {
        val start: Int = pageable.offset.toInt()
        val end: Int = (start + pageable.pageSize).coerceAtMost(this.size)
        val content: List<T> = if (start <= end) this.subList(start, end) else emptyList()
        return PageImpl(content, pageable, this.size.toLong())
    }

}
