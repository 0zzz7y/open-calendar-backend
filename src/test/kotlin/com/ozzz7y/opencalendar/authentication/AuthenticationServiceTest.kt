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
    private val _hashed = "hashedPw"

    @BeforeEach
    fun setUp() {
        _service = AuthenticationService(_userRepository, _jwtService, _passwordEncoder)

        _registerRequest = RegisterRequest(
            username = "john",
            email = "john@example.com",
            password = "P@ssw0rd!"
        )
        _loginRequest = LoginRequest(username = "john", password = "P@ssw0rd!")

        _user = User(
            id = UUID.randomUUID(),
            username = _registerRequest.username,
            email = _registerRequest.email,
            password = _hashed
        )
    }

    // ------------------------------------------------------------------
    // register()
    // ------------------------------------------------------------------

    @Test
    fun `should register user`() {
        whenever(_userRepository.findByUsername(_registerRequest.username)).thenReturn(null)
        whenever(_userRepository.findByEmail(_registerRequest.email)).thenReturn(null)
        whenever(_passwordEncoder.encode(_registerRequest.password)).thenReturn(_hashed)
        whenever(_userRepository.save(any<User>())).thenAnswer { it.arguments[0] }

        _service.register(_registerRequest)

        verify(_passwordEncoder).encode(_registerRequest.password)
        verify(_userRepository).save(argThat { username == "john" && password == _hashed })
    }

    @Test
    fun `should throw error when username exists`() {
        whenever(_userRepository.findByUsername(_registerRequest.username)).thenReturn(_user)

        assertThrows<IllegalArgumentException> { _service.register(_registerRequest) }

        verify(_userRepository, never()).save(any())
    }

    @Test
    fun `should throw error when email already in use`() {
        whenever(_userRepository.findByUsername(_registerRequest.username)).thenReturn(null)
        whenever(_userRepository.findByEmail(_registerRequest.email)).thenReturn(_user)

        assertThrows<IllegalArgumentException> { _service.register(_registerRequest) }

        verify(_userRepository, never()).save(any())
    }

    // ------------------------------------------------------------------
    // login()
    // ------------------------------------------------------------------

    @Test
    fun `should login user and return token`() {
        val token = "jwt-token"
        whenever(_userRepository.findByUsername(_loginRequest.username)).thenReturn(_user)
        whenever(_passwordEncoder.matches(_loginRequest.password, _hashed)).thenReturn(true)
        whenever(_jwtService.generateToken(_user.id)).thenReturn(token)

        val result = _service.login(_loginRequest)

        assertEquals(token, result)
        verify(_jwtService).generateToken(_user.id)
    }

    @Test
    fun `should throw error when username not found during login`() {
        whenever(_userRepository.findByUsername(_loginRequest.username)).thenReturn(null)

        assertThrows<IllegalArgumentException> { _service.login(_loginRequest) }

        verify(_jwtService, never()).generateToken(any())
    }

    @Test
    fun `should throw error when password mismatch during login`() {
        whenever(_userRepository.findByUsername(_loginRequest.username)).thenReturn(_user)
        whenever(_passwordEncoder.matches(_loginRequest.password, _hashed)).thenReturn(false)

        assertThrows<IllegalArgumentException> { _service.login(_loginRequest) }

        verify(_jwtService, never()).generateToken(any())
    }

    // ------------------------------------------------------------------
    // logout()
    // ------------------------------------------------------------------

    @Test
    fun `should logout user and invalidate token`() {
        val token = "jwt-token"
        whenever(_httpRequest.getHeader("Authorization")).thenReturn("Bearer $token")
        doNothing().whenever(_jwtService).invalidate(token)

        _service.logout(_httpRequest)

        verify(_jwtService).invalidate(token)
    }

    @Test
    fun `should throw error when Authorization header missing on logout`() {
        whenever(_httpRequest.getHeader("Authorization")).thenReturn(null)

        assertThrows<IllegalArgumentException> { _service.logout(_httpRequest) }

        verify(_jwtService, never()).invalidate(any())
    }

    @Test
    fun `should throw error when Authorization header format invalid on logout`() {
        whenever(_httpRequest.getHeader("Authorization")).thenReturn("Token xyz")

        assertThrows<IllegalArgumentException> { _service.logout(_httpRequest) }

        verify(_jwtService, never()).invalidate(any())
    }
}
