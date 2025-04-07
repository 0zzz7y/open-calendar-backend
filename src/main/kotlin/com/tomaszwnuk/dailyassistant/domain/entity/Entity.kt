package com.tomaszwnuk.dailyassistant.domain.entity

import com.tomaszwnuk.dailyassistant.validation.FieldConstraints.COLUMN_DEFINITION_DATE
import com.tomaszwnuk.dailyassistant.validation.FieldConstraints.COLUMN_DEFINITION_ID
import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class Entity(

    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    val id: UUID = UUID.randomUUID()

) {

    @CreatedDate
    @Column(columnDefinition = COLUMN_DEFINITION_DATE, updatable = false, nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    @Column(columnDefinition = COLUMN_DEFINITION_DATE, updatable = false, nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()

}
