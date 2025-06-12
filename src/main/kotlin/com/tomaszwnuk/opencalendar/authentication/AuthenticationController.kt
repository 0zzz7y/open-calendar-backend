package com.tomaszwnuk.opencalendar.authentication

import com.tomaszwnuk.opencalendar.authentication.request.AuthenticationResponse
import com.tomaszwnuk.opencalendar.authentication.request.LoginRequest
import com.tomaszwnuk.opencalendar.authentication.request.RegisterRequest
import com.tomaszwnuk.opencalendar.domain.communication.CommunicationConstants
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
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<Unit> {
        _authenticationService.register(request)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthenticationResponse> {
        val token: String = _authenticationService.login(request)
        return ResponseEntity.ok(AuthenticationResponse(token))
    }
}