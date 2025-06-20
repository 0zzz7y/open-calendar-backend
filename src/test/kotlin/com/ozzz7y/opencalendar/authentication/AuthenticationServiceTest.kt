package com.ozzz7y.opencalendar.authentication

import com.ozzz7y.opencalendar.authentication.request.LoginRequest
import com.ozzz7y.opencalendar.authentication.request.RegisterRequest
import com.ozzz7y.opencalendar.domain.user.User
import com.ozzz7y.opencalendar.domain.user.UserRepository
import com.ozzz7y.opencalendar.security.JwtService
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
internal class AuthenticationServiceTest {

    @Mock
    private lateinit var _userRepository: UserRepository

    @Mock
    private lateinit var _jwtService: JwtService

    @Mock
    private lateinit var _passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var _httpRequest: HttpServletRequest

    private lateinit var _service: AuthenticationService

    private lateinit var _registerRequest: RegisterRequest

    private lateinit var _loginRequest: LoginRequest

    private lateinit var _user: User

    private val _hashedPassword = "hashed-password"

    @BeforeEach
    fun setUp() {
        _service = AuthenticationService(
            _userRepository = _userRepository,
            _jwtService = _jwtService,
            _passwordEncoder = _passwordEncoder
        )

        _registerRequest = RegisterRequest(
            username = "test",
            email = "test@example.com",
            password = "password"
        )
        _loginRequest = LoginRequest(
            username = "test",
            password = "password"
        )

        _user = User(
            id = UUID.randomUUID(),
            username = _registerRequest.username,
            email = _registerRequest.email,
            password = _hashedPassword
        )
    }

    @Test
    fun `should register user`() {
        whenever(_userRepository.findByUsername(username = _registerRequest.username)).thenReturn(null)
        whenever(_userRepository.findByEmail(email = _registerRequest.email)).thenReturn(null)
        whenever(_passwordEncoder.encode(_registerRequest.password)).thenReturn(_hashedPassword)
        whenever(_userRepository.save(any<User>())).thenAnswer { it.arguments[0] }

        _service.register(request = _registerRequest)

        verify(_passwordEncoder).encode(_registerRequest.password)
        verify(_userRepository).save(any<User>())
    }

    @Test
    fun `should throw error when username exists`() {
        whenever(_userRepository.findByUsername(username = _registerRequest.username)).thenReturn(_user)

        assertThrows<IllegalArgumentException> { _service.register(request = _registerRequest) }

        verify(_userRepository, never()).save(any())
    }

    @Test
    fun `should throw error when email already in use`() {
        whenever(_userRepository.findByUsername(username = _registerRequest.username)).thenReturn(null)
        whenever(_userRepository.findByEmail(email = _registerRequest.email)).thenReturn(_user)

        assertThrows<IllegalArgumentException> { _service.register(request = _registerRequest) }

        verify(_userRepository, never()).save(any())
    }

    @Test
    fun `should login user and return token`() {
        val token = "jwt-token"

        whenever(_userRepository.findByUsername(username = _loginRequest.username)).thenReturn(_user)
        whenever(_passwordEncoder.matches(_loginRequest.password, _hashedPassword)).thenReturn(true)
        whenever(_jwtService.generateToken(_user.id)).thenReturn(token)

        val result: String = _service.login(request = _loginRequest)

        assertEquals(token, result)

        verify(_jwtService).generateToken(_user.id)
    }

    @Test
    fun `should throw error when username not found during login`() {
        whenever(_userRepository.findByUsername(username = _loginRequest.username)).thenReturn(null)

        assertThrows<IllegalArgumentException> { _service.login(request = _loginRequest) }

        verify(_jwtService, never()).generateToken(any())
    }

    @Test
    fun `should throw error when password mismatch during login`() {
        whenever(_userRepository.findByUsername(username = _loginRequest.username)).thenReturn(_user)
        whenever(_passwordEncoder.matches(_loginRequest.password, _hashedPassword)).thenReturn(false)

        assertThrows<IllegalArgumentException> { _service.login(request = _loginRequest) }

        verify(_jwtService, never()).generateToken(any())
    }

    @Test
    fun `should logout user and invalidate token`() {
        val token = "jwt-token"

        whenever(_httpRequest.getHeader(AUTHORIZATION_HEADER)).thenReturn("Bearer $token")
        doNothing().whenever(_jwtService).invalidate(token = token)

        _service.logout(request = _httpRequest)

        verify(_jwtService).invalidate(token = token)
    }

    @Test
    fun `should throw error when Authorization header missing on logout`() {
        whenever(_httpRequest.getHeader(AUTHORIZATION_HEADER)).thenReturn(null)

        assertThrows<IllegalArgumentException> { _service.logout(_httpRequest) }

        verify(_jwtService, never()).invalidate(any())
    }

    @Test
    fun `should throw error when Authorization header format invalid on logout`() {
        whenever(_httpRequest.getHeader(AUTHORIZATION_HEADER)).thenReturn("Token xyz")

        assertThrows<IllegalArgumentException> { _service.logout(_httpRequest) }

        verify(_jwtService, never()).invalidate(any())
    }

    companion object {

        const val AUTHORIZATION_HEADER: String = "Authorization"

    }

}
