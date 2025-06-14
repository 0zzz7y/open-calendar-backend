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

/**
 * The controller for managing authentication.
 */
@Suppress("unused")
@RestController
@RequestMapping("/${CommunicationConstants.API}/${CommunicationConstants.API_VERSION}/authentication")
class AuthenticationController(

    /**
     * The service for performing authentication operations.
     */
    private val _authenticationService: AuthenticationService

) {

    /**
     * Registers a new user.
     *
     * @param request The registration request containing user details
     *
     * @return A response indicating the success of the registration
     */
    @PostMapping("/register")
    fun register(@Valid @RequestBody(required = true) request: RegisterRequest): ResponseEntity<Unit> {
        _authenticationService.register(request = request)
        return ResponseEntity.ok().build()
    }

    /**
     * Logs in a user.
     *
     * @param request The login request containing user credentials
     *
     * @return A response containing the authentication token
     */
    @PostMapping("/login")
    fun login(@Valid @RequestBody(required = true) request: LoginRequest): ResponseEntity<AuthenticationResponse> {
        val token: String = _authenticationService.login(request = request)
        return ResponseEntity.ok(AuthenticationResponse(token))
    }

    /**
     * Logs out a user.
     *
     * @param request The request containing the user's session information
     *
     * @return A response indicating the success of the logout operation
     */
    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): ResponseEntity<Unit> {
        _authenticationService.logout(request)
        return ResponseEntity.ok().build()
    }

}
