package com.tomaszwnuk.opencalendar.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import java.util.UUID

@Component
class JwtService(
    @Value("\${jwt.secret}") private val _secret: String,
) {

    private val _key = Keys.hmacShaKeyFor(_secret.toByteArray())

    fun generateToken(userId: UUID): String = Jwts.builder()
        .subject(userId.toString())
        .issuedAt(Date())
        .expiration(Date(System.currentTimeMillis() + AUTHORIZATION_TIMEOUT))
        .signWith(_key)
        .compact()

    fun extractUserId(token: String): UUID? = try {
        val claims = Jwts.parser()
            .verifyWith(_key)
            .build()
            .parseSignedClaims(token)
            .payload
        UUID.fromString(claims.subject)
    } catch (_: Exception) {
        null
    }

    companion object {

        const val AUTHORIZATION_TIMEOUT: Long = 1000 * 60 * 60 * 24

    }

}
