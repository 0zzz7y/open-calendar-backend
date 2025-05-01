/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.common.date

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * A JPA attribute converter that converts between `LocalDateTime` and `Long` (epoch milliseconds).
 * This converter is automatically applied to all `LocalDateTime` fields in entities.
 */
@Converter(autoApply = true)
class LocalDateTimeConverter : AttributeConverter<LocalDateTime, Long> {

    /**
     * Converts a `LocalDateTime` attribute to its database representation as epoch milliseconds.
     *
     * @param attribute The `LocalDateTime` value to be converted.
     *
     * @return The epoch milliseconds representation of the `LocalDateTime`, or `null` if the input is `null`.
     */
    override fun convertToDatabaseColumn(attribute: LocalDateTime?): Long? =
        attribute?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()

    /**
     * Converts a database column value (epoch milliseconds) to a `LocalDateTime` entity attribute.
     *
     * @param data The epoch milliseconds value from the database.
     *
     * @return The corresponding `LocalDateTime` value, or `null` if the input is `null`.
     */
    override fun convertToEntityAttribute(data: Long?): LocalDateTime? =
        data?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime() }

}
