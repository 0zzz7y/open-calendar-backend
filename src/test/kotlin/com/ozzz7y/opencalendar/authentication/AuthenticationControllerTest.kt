package com.ozzz7y.opencalendar.authentication

import com.ozzz7y.opencalendar.authentication.request.AuthenticationResponse
import com.ozzz7y.opencalendar.authentication.request.LoginRequest
import com.ozzz7y.opencalendar.authentication.request.RegisterRequest
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
internal class AuthenticationControllerTest {

    @Mock
    private lateinit var _service: AuthenticationService

    @Mock
    private lateinit var _httpRequest: HttpServletRequest

    @InjectMocks
    private lateinit var _controller: AuthenticationController

    private lateinit var _registerRequest: RegisterRequest
    private lateinit var _loginRequest: LoginRequest

    @BeforeEach
    fun setUp() {
        _registerRequest = RegisterRequest(
            username = "john_doe",
            email = "john@example.com",
            password = "P@ssw0rd!"
        )
        _loginRequest = LoginRequest(
            username = "john_doe",
            password = "P@ssw0rd!"
        )
    }

    @Test
    fun `should register user and return status code 200 OK`() {
        doNothing().whenever(_service).register(eq(_registerRequest))

        val response: ResponseEntity<Unit> = _controller.register(request = _registerRequest)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(null, response.body)

        verify(_service).register(eq(_registerRequest))
    }

    @Test
    fun `should login user and return token with status code 200 OK`() {
        val token = "jwt-token-value"
        whenever(_service.login(eq(_loginRequest))).thenReturn(token)

        val response: ResponseEntity<AuthenticationResponse> =
            _controller.login(request = _loginRequest)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(token, response.body!!.token)

        verify(_service).login(eq(_loginRequest))
    }

    @Test
    fun `should logout user and return status code 200 OK`() {
        doNothing().whenever(_service).logout(_httpRequest)

        val response: ResponseEntity<Unit> = _controller.logout(request = _httpRequest)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(null, response.body)

        verify(_service).logout(_httpRequest)
    }
}
