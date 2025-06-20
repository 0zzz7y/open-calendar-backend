package com.ozzz7y.opencalendar.authentication

import com.ozzz7y.opencalendar.authentication.request.LoginRequest
import com.ozzz7y.opencalendar.authentication.request.RegisterRequest
import com.ozzz7y.opencalendar.domain.user.User
import com.ozzz7y.opencalendar.domain.user.UserRepository
import com.ozzz7y.opencalendar.security.JwtService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 * The service for performing authentication operations.
 */
@Service
class AuthenticationService(

    /**
     * The repository for managing user data.
     */
    private val _userRepository: UserRepository,

    /**
     * The service for performing JWT operations.
     */
    private val _jwtService: JwtService,

    /**
     * The password encoder for hashing passwords.
     */
    private val _passwordEncoder: PasswordEncoder

) {

    /**
     * Registers a new user.
     *
     * @param request The registration request containing user details
     *
     * @throws IllegalArgumentException If the user with given username or email already exists
     */
    fun register(request: RegisterRequest) {
        if (_userRepository.findByUsername(request.username) != null) {
            throw IllegalArgumentException("User already exists.")
        }

        if (_userRepository.findByEmail(request.email) != null) {
            throw IllegalArgumentException("Email already in use.")
        }

        val user = User(
            email = request.email,
            username = request.username,
            password = _passwordEncoder.encode(request.password)
        )

        _userRepository.save(user)
    }

    /**
     * Logs in a user.
     *
     * @param request The login request containing user credentials
     *
     * @return A JWT token for the authenticated user
     *
     * @throws IllegalArgumentException If credentials are not valid
     */
    fun login(request: LoginRequest): String {
        val user = _userRepository.findByUsername(request.username)
            ?: throw IllegalArgumentException("Invalid credentials.")

        if (!_passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("Invalid credentials.")
        }

        return _jwtService.generateToken(user.id)
    }

    /**
     * Logs out a user by invalidating their JWT token.
     *
     * @param request The request containing the user's session information
     *
     * @throws IllegalArgumentException If the authorization token is not valid
     */
    fun logout(request: HttpServletRequest) {
        val header: String = request.getHeader("Authorization")
            ?: throw IllegalArgumentException("Missing Authorization header.")

        if (!header.startsWith("Bearer ")) {
            throw IllegalArgumentException("Invalid Authorization header format.")
        }

        val token: String = header.removePrefix("Bearer ").trim()
        _jwtService.invalidate(token)
    }

}
