/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.utility.throttling

import com.tomaszwnuk.opencalendar.domain.communication.CommunicationConstraints.MAXIMUM_REQUESTS_PER_MINUTE
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Component
class ThrottlingFilter : Filter {

    private val _buckets: MutableMap<String, Bucket> = ConcurrentHashMap()

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val httpRequest: HttpServletRequest = request as HttpServletRequest
        val httpResponse: HttpServletResponse = response as HttpServletResponse
        val clientIp: String = getClientIp(httpRequest)
        val bucket: Bucket = _buckets.computeIfAbsent(clientIp) { createBucket() }

        if (bucket.tryConsume(1)) {
            chain?.doFilter(request, response)
        } else {
            httpResponse.status = 429
            httpResponse.writer.write("Too many requests. Try again later.")
        }
    }

    private fun createBucket(): Bucket {
        val limit: Bandwidth = Bandwidth.classic(
            MAXIMUM_REQUESTS_PER_MINUTE,
            Refill.greedy(MAXIMUM_REQUESTS_PER_MINUTE, Duration.ofMinutes(1))
        )
        return Bucket.builder().addLimit(limit).build()
    }

    /**
     * Retrieves the client IP address from the request.
     * Checks the "X-Forwarded-For" header first, falling back to the remote address if not present.
     *
     * @param request The HTTP servlet request.
     * @return The client IP address as a string.
     */
    private fun getClientIp(request: HttpServletRequest): String {
        return request.getHeader("X-Forwarded-For") ?: request.remoteAddr
    }

}
