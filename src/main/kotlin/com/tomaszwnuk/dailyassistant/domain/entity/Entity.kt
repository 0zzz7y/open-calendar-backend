package com.tomaszwnuk.dailyassistant.domain.entity

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
abstract class Entity {

    @Id
    @Column(columnDefinition = "CHAR(36)", nullable = false)
    val id: UUID = UUID.randomUUID()

    @CreatedDate
    @Column(updatable = false)
    var createdAt: LocalDateTime? = null

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null

}
