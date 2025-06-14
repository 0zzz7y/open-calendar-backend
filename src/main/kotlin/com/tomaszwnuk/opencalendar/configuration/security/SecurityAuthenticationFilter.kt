package com.tomaszwnuk.opencalendar.configuration.security

import com.tomaszwnuk.opencalendar.authentication.TokenBlackList
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

@Component
class SecurityAuthenticationFilter(
    private val _jwtService: JwtService,
    private val _userRepository: UserRepository,
    private val _tokenBlacklist: TokenBlackList
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header: String? = request.getHeader("Authorization")
        if (header.isNullOrBlank() || !header.startsWith(HEADER_PREFIX)) {
            return filterChain.doFilter(request, response)
        }

        val token: String = header.removePrefix(HEADER_PREFIX).trim()
        if (_tokenBlacklist.isInvalid(token)) {
            return filterChain.doFilter(request, response)
        }

        val userId: UUID? = _jwtService.extractUserId(token)
        if (userId == null || SecurityContextHolder.getContext().authentication != null) {
            return filterChain.doFilter(request, response)
        }

        val user: User? = _userRepository.findById(userId).orElse(null)
        if (user == null) {
            return filterChain.doFilter(request, response)
        }

        val authentication = UsernamePasswordAuthenticationToken(user, null, emptyList())
        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

        SecurityContextHolder.getContext().authentication = authentication
        filterChain.doFilter(request, response)
    }

    companion object {

        const val HEADER_PREFIX: String = "Bearer "

    }

}
