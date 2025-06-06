/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.entity

import com.tomaszwnuk.opencalendar.common.date.LocalDateTimeConverter
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_DATE
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_ID
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

@Suppress("unused")
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class Entity(

    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    val id: UUID = UUID.randomUUID()

) : Serializable {

    @CreatedDate
    @Convert(converter = LocalDateTimeConverter::class)
    @Column(columnDefinition = COLUMN_DEFINITION_DATE, updatable = false, nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    @Convert(converter = LocalDateTimeConverter::class)
    @Column(columnDefinition = COLUMN_DEFINITION_DATE, updatable = true, nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()

}
