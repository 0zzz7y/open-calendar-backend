package com.tomaszwnuk.opencalendar.authentication

import com.tomaszwnuk.opencalendar.authentication.request.LoginRequest
import com.tomaszwnuk.opencalendar.authentication.request.RegisterRequest
import com.tomaszwnuk.opencalendar.domain.user.User
import com.tomaszwnuk.opencalendar.domain.user.UserRepository
import com.tomaszwnuk.opencalendar.security.JwtService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val _userRepository: UserRepository,
    private val _passwordEncoder: PasswordEncoder,
    private val _jwtService: JwtService
) {

    fun register(request: RegisterRequest) {
        if (_userRepository.findByUsername(request.username) != null) {
            throw IllegalArgumentException("User already exists")
        }

        val user = User(
            email = request.email,
            username = request.username,
            password = _passwordEncoder.encode(request.password)
        )

        _userRepository.save(user)
    }

    fun login(request: LoginRequest): String {
        val user = _userRepository.findByUsername(request.username)
            ?: throw IllegalArgumentException("Invalid credentials")

        if (!_passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("Invalid credentials")
        }

        return _jwtService.generateToken(user.id)
    }
}