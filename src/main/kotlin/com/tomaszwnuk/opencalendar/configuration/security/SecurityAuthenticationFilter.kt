package com.tomaszwnuk.opencalendar.configuration.security

import com.tomaszwnuk.opencalendar.authentication.TokenBlackListService
import com.tomaszwnuk.opencalendar.domain.user.User
import com.tomaszwnuk.opencalendar.domain.user.UserRepository
import com.tomaszwnuk.opencalendar.security.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

/**
 * The security authentication filter.
 */
@Component
class SecurityAuthenticationFilter(

    /**
     * The service for performing JWT operations.
     */
    private val _jwtService: JwtService,

    /**
     * The repository for managing user data.
     */
    private val _userRepository: UserRepository,

    /**
     * The service for performing operations on the token blacklist.
     */
    private val _tokenBlacklistService: TokenBlackListService

) : OncePerRequestFilter() {

    /**
     * Filters the incoming request to authenticate the user based on the JWT token.
     *
     * @param request The HTTP servlet request
     * @param response The HTTP servlet response
     * @param filterChain The filter chain to continue processing the request
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Header exists and is correct
        val header: String? = request.getHeader("Authorization")
        val isHeaderCorrect: Boolean = (header != null && header.isNotBlank() && header.startsWith(HEADER_PREFIX))
        if (!isHeaderCorrect) {
            return filterChain.doFilter(request, response)
        }

        // Token is valid and not blacklisted
        val token: String = header?.removePrefix(HEADER_PREFIX)!!.trim()
        val isTokenInvalid: Boolean = (_tokenBlacklistService.isInvalid(token))
        if (isTokenInvalid) {
            return filterChain.doFilter(request, response)
        }

        // Extract user ID from token
        val userId: UUID? = _jwtService.extractUserId(token)
        val isUserIdValid: Boolean = (userId != null)
        if (!isUserIdValid) {
            return filterChain.doFilter(request, response)
        }

        // Check if authentication is already valid
        val isAuthenticationValid: Boolean = (SecurityContextHolder.getContext().authentication != null)
        if (isAuthenticationValid) {
            return filterChain.doFilter(request, response)
        }

        // Load user from repository
        val user: Optional<User> = _userRepository.findById(userId!!)
        if (!user.isPresent) {
            return filterChain.doFilter(request, response)
        }

        // Authenticate user
        val authentication = UsernamePasswordAuthenticationToken(user.get(), null, emptyList())
        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authentication
        filterChain.doFilter(request, response)
    }

    companion object {

        /**
         * The prefix for the authorization header.
         */
        const val HEADER_PREFIX: String = "Bearer "

    }

}
