package com.tomaszwnuk.opencalendar.authentication

import com.tomaszwnuk.opencalendar.authentication.request.AuthenticationResponse
import com.tomaszwnuk.opencalendar.authentication.request.LoginRequest
import com.tomaszwnuk.opencalendar.authentication.request.RegisterRequest
import com.tomaszwnuk.opencalendar.domain.communication.CommunicationConstants
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Suppress("unused")
@RestController
@RequestMapping("/${CommunicationConstants.API}/${CommunicationConstants.API_VERSION}/authentication")
class AuthenticationController(
    private val _authenticationService: AuthenticationService
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody(required = true) request: RegisterRequest): ResponseEntity<Unit> {
        _authenticationService.register(request = request)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody(required = true) request: LoginRequest): ResponseEntity<AuthenticationResponse> {
        val token: String = _authenticationService.login(request = request)
        return ResponseEntity.ok(AuthenticationResponse(token))
    }

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): ResponseEntity<Unit> {
        _authenticationService.logout(request)
        return ResponseEntity.ok().build()
    }

}
