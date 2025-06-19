package com.ozzz7y.opencalendar.configuration.throttling

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

/**
 * The throttling filter that limits the number of requests per minute from a single client IP address.
 */
@Component
class ThrottlingFilter : Filter {

    /**
     * The map of client IP addresses to their respective request buckets.
     */
    private val _buckets: MutableMap<String, Bucket> = ConcurrentHashMap()

    /**
     * Filters incoming requests to enforce throttling based on client IP address.
     *
     * @param request The servlet request
     * @param response The servlet response
     * @param chain The filter chain to continue processing the request
     */
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val httpRequest: HttpServletRequest = request as HttpServletRequest
        val httpResponse: HttpServletResponse = response as HttpServletResponse
        val clientIp: String = getClientIp(httpRequest)
        val bucket: Bucket = _buckets.computeIfAbsent(clientIp) { createBucket() }
        val numberOfTokensToConsume: Long = 1

        if (bucket.tryConsume(numberOfTokensToConsume)) {
            chain?.doFilter(request, response)
        } else {
            httpResponse.status = TOO_MANY_REQUESTS_HTTP_STATUS
            httpResponse.writer.write(TOO_MANY_REQUESTS_MESSAGE)
        }
    }

    /**
     * Creates a new bucket with a limit of requests per minute.
     *
     * @return A new bucket with the defined limits
     */
    private fun createBucket(): Bucket {
        val limit: Bandwidth = Bandwidth.classic(
            MAXIMUM_REQUESTS_PER_MINUTE,
            Refill.greedy(MAXIMUM_REQUESTS_PER_MINUTE, Duration.ofMinutes(1))
        )
        return Bucket.builder().addLimit(limit).build()
    }

    /**
     * Retrieves the client IP address from the request.
     *
     * @param request The HTTP servlet request
     *
     * @return The client IP address
     */
    private fun getClientIp(request: HttpServletRequest): String {
        return request.getHeader("X-Forwarded-For") ?: request.remoteAddr
    }

    companion object {

        /**
         * The maximum number of requests allowed per minute from a single client IP address.
         */
        const val MAXIMUM_REQUESTS_PER_MINUTE: Long = 60

        /**
         * The message to return when the request limit is exceeded.
         */
        const val TOO_MANY_REQUESTS_MESSAGE: String = "Too many requests. Try again later."

        /**
         * The HTTP status code for too many requests.
         */
        const val TOO_MANY_REQUESTS_HTTP_STATUS: Int = 429

    }

}
