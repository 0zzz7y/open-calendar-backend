package com.tomaszwnuk.opencalendar.domain.user

import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_ID
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_USER_EMAIL
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_USER_NAME
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.COLUMN_DEFINITION_USER_PASSWORD
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "_user")
class User(

    @Id
    @Column(columnDefinition = COLUMN_DEFINITION_ID, nullable = false, updatable = false)
    override val id: UUID = UUID.randomUUID(),

    @Column(columnDefinition = COLUMN_DEFINITION_USER_EMAIL, unique = true, nullable = false)
    val email: String,

    @Column(columnDefinition = COLUMN_DEFINITION_USER_NAME, unique = true, nullable = false)
    val username: String,

    @Column(columnDefinition = COLUMN_DEFINITION_USER_PASSWORD, unique = false, nullable = false)
    val password: String

) : com.tomaszwnuk.opencalendar.domain.entity.Entity()
