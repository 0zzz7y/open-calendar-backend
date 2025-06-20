package com.ozzz7y.opencalendar.domain.entity

import com.ozzz7y.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_DATE
import com.ozzz7y.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_ID
import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

/**
 * The base entity class.
 */
@Suppress("unused")
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class Entity(

    /**
     * The unique identifier of the entity.
     */
    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    val id: UUID = UUID.randomUUID()

) : Serializable {

    /**
     * The date and time when the entity was created.
     */
    @CreatedDate
    @Column(columnDefinition = COLUMN_DEFINITION_DATE, updatable = false, nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    /**
     * The date and time when the entity was last updated.
     */
    @LastModifiedDate
    @Column(columnDefinition = COLUMN_DEFINITION_DATE, updatable = true, nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()

}
