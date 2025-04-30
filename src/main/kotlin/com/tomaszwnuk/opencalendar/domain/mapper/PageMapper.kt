package com.tomaszwnuk.opencalendar.domain.mapper

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

object PageMapper {

    fun <T> List<T>.toPage(pageable: Pageable): Page<T> {
        val start = pageable.offset.toInt()
        val end = (start + pageable.pageSize).coerceAtMost(this.size)
        val content = if (start <= end) this.subList(start, end) else emptyList()
        return PageImpl(content, pageable, this.size.toLong())
    }

}
