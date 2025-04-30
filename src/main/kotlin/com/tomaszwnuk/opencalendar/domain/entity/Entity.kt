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

/**
 * Abstract base class for entities in the application.
 * Provides common fields and functionality for all entities, such as ID, creation timestamp, and update timestamp.
 * Uses JPA annotations for persistence and Spring Data annotations for auditing.
 */
@Suppress("unused")
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class Entity(

    /**
     * The unique identifier for the entity.
     * Automatically generated as a UUID and cannot be updated.
     */
    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    val id: UUID = UUID.randomUUID()

) : Serializable {

    /**
     * The timestamp when the entity was created.
     * Automatically set and cannot be updated.
     */
    @CreatedDate
    @Convert(converter = LocalDateTimeConverter::class)
    @Column(columnDefinition = COLUMN_DEFINITION_DATE, updatable = false, nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    /**
     * The timestamp when the entity was last updated.
     * Automatically set and cannot be updated.
     */
    @LastModifiedDate
    @Convert(converter = LocalDateTimeConverter::class)
    @Column(columnDefinition = COLUMN_DEFINITION_DATE, updatable = false, nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()

}
