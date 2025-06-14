package com.tomaszwnuk.opencalendar.domain.mapper

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

/**
 * The mapper for converting an object to a map with an additional type field.
 */
object ItemTypeMapper {

    /**
     * Converts an object to a map and adds a type field.
     *
     * @param type The type to be added to the map
     *
     * @return A map representation of the object with the type field included
     */
    fun Any.toMapWithType(type: String): Map<String, Any> {
        val map: Map<String, Any> =
            jacksonObjectMapper().registerModule(JavaTimeModule()).convertValue<Map<String, Any>>(this)
        val mapWithType: Map<String, Any> = map + mapOf("type" to type)
        return mapWithType
    }

}
