package com.tomaszwnuk.opencalendar.domain.mapper

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object ItemTypeMapper {

    fun Any.toMapWithType(type: String): Map<String, Any> {
        val map: Map<String, Any> =
            jacksonObjectMapper().registerModule(JavaTimeModule()).convertValue<Map<String, Any>>(this)
        val mapWithType: Map<String, Any> = map + mapOf("type" to type)
        return mapWithType
    }

}
