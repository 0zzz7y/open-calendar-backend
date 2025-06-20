package com.ozzz7y.opencalendar.security

import com.ozzz7y.opencalendar.authentication.TokenBlackListService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

/**
 * The service for performing JWT operations.
 */
@Component
class JwtService(

    /**
     * The secret key used for signing the JWT tokens.
     */
    @Value("\${jwt.secret}") private val _secret: String,
    private val _blackList: TokenBlackListService

) {

    /**
     * The key used for signing the JWT tokens.
     */
    private val _key = Keys.hmacShaKeyFor(_secret.toByteArray())

    /**
     * Generates a JWT token for the given user ID.
     *
     * @param userId The unique identifier of the user
     *
     * @return The generated JWT token
     */
    fun generateToken(userId: UUID): String = Jwts.builder()
        .subject(userId.toString())
        .issuedAt(Date())
        .expiration(Date(System.currentTimeMillis() + AUTHORIZATION_TIMEOUT))
        .signWith(_key)
        .compact()

    /**
     * Extracts the user unique identifier from the given JWT token.
     *
     * @param token The JWT token from which to extract the user unique identifier
     *
     * @return The unique identifier of the user if the token is valid, null otherwise
     */
    fun extractUserId(token: String): UUID? = try {
        val claims: Claims = Jwts.parser()
            .verifyWith(_key)
            .build()
            .parseSignedClaims(token)
            .payload
        UUID.fromString(claims.subject)
    } catch (_: Exception) {
        null
    }

    /**
     * Invalidates the given JWT token by adding it to the blacklist.
     *
     * @param token The JWT token to invalidate
     */
    fun invalidate(token: String) {
        _blackList.invalidate(token)
    }

    companion object {

        /**
         * The authorization timeout in milliseconds.
         */
        const val AUTHORIZATION_TIMEOUT: Long = 1000 * 60 * 60 * 24

    }

}
