package com.tomaszwnuk.opencalendar.authentication.request

import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.USER_NAME_MAXIMUM_LENGTH
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.USER_PASSWORD_MAXIMUM_LENGTH
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.USER_PASSWORD_MINIMUM_LENGTH
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(

    @field:NotBlank(message = "Username cannot be blank.")
    @field:Size(max = USER_NAME_MAXIMUM_LENGTH)
    val username: String,

    @field:NotBlank(message = "Password cannot be blank.")
    @field:Size(
        min = USER_PASSWORD_MINIMUM_LENGTH,
        max = USER_PASSWORD_MAXIMUM_LENGTH
    )
    val password: String

)
