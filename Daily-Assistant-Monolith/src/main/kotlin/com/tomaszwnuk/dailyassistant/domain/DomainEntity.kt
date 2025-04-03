package com.tomaszwnuk.dailyassistant.domain

import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import java.util.UUID

@MappedSuperclass
abstract class DomainEntity {

    @Id
    @Column(columnDefinition = "CHAR(36)")
    val id: UUID = UUID.randomUUID()
}
