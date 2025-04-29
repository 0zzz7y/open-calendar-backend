package com.tomaszwnuk.opencalendar.domain.date

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Converter(autoApply = true)
class LocalDateTimeConverter : AttributeConverter<LocalDateTime, Long> {

    override fun convertToDatabaseColumn(attribute: LocalDateTime?): Long? = attribute?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()

    override fun convertToEntityAttribute(dbData: Long?): LocalDateTime? = dbData?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime() }

}
