package com.tomaszwnuk.dailyassistant.domain

import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import java.util.*

@MappedSuperclass
abstract class Entity {

    @Id
    @Column(columnDefinition = "CHAR(36)", nullable = false)
    val id: UUID = UUID.randomUUID()

}
