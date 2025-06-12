package com.tomaszwnuk.opencalendar.authentication.request

import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.USER_EMAIL_MAXIMUM_LENGTH
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.USER_NAME_MAXIMUM_LENGTH
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.USER_PASSWORD_MAXIMUM_LENGTH
import com.tomaszwnuk.opencalendar.domain.field.FieldConstraints.USER_PASSWORD_MINIMUM_LENGTH
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(

    @field:NotBlank(message = "Username cannot be blank.")
    @field:Size(
        max = USER_NAME_MAXIMUM_LENGTH,
        message = "Username cannot be longer than $USER_NAME_MAXIMUM_LENGTH characters."
    )
    val username: String,

    @field:NotBlank(message = "Email cannot be blank.")
    @field:Email(message = "Invalid email format.")
    @field:Size(
        max = USER_EMAIL_MAXIMUM_LENGTH,
        message = "Email cannot be longer than $USER_EMAIL_MAXIMUM_LENGTH characters."
    )
    val email: String,

    @field:NotBlank(message = "Password cannot be blank.")
    @field:Size(
        min = USER_PASSWORD_MINIMUM_LENGTH,
        max = USER_PASSWORD_MAXIMUM_LENGTH,
        message = "Password must be between $USER_PASSWORD_MINIMUM_LENGTH and $USER_PASSWORD_MAXIMUM_LENGTH characters."
    )
    val password: String

)
