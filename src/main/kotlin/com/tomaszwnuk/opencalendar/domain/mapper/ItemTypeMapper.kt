/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.mapper

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

/**
 * Utility object for mapping objects to a map representation with an additional type field.
 */
object ItemTypeMapper {

    /**
     * Converts an object to a map representation and adds a "type" field to the map.
     *
     * @receiver The object to be converted to a map.
     * @param type The type to be added to the map as a "type" field.
     * @return A map representation of the object with an additional "type" field.
     */
    fun Any.toMapWithType(type: String): Map<String, Any> {
        val map: Map<String, Any> =
            jacksonObjectMapper().registerModule(JavaTimeModule()).convertValue<Map<String, Any>>(this)
        val mapWithType: Map<String, Any> = map + mapOf("type" to type)
        return mapWithType
    }

}
