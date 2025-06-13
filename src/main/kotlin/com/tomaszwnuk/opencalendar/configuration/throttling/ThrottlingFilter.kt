package com.tomaszwnuk.opencalendar.configuration.throttling

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
        val numberOfTokensToConsume: Long = 1

        if (bucket.tryConsume(numberOfTokensToConsume)) {
            chain?.doFilter(request, response)
        } else {
            httpResponse.status = TOO_MANY_REQUESTS_HTTP_STATUS
            httpResponse.writer.write(TOO_MANY_REQUESTS_MESSAGE)
        }
    }

    private fun createBucket(): Bucket {
        val limit: Bandwidth = Bandwidth.classic(
            MAXIMUM_REQUESTS_PER_MINUTE,
            Refill.greedy(MAXIMUM_REQUESTS_PER_MINUTE, Duration.ofMinutes(1))
        )
        return Bucket.builder().addLimit(limit).build()
    }

    private fun getClientIp(request: HttpServletRequest): String {
        return request.getHeader("X-Forwarded-For") ?: request.remoteAddr
    }

    companion object {

        const val MAXIMUM_REQUESTS_PER_MINUTE: Long = 60

        const val TOO_MANY_REQUESTS_HTTP_STATUS: Int = 429

        const val TOO_MANY_REQUESTS_MESSAGE: String = "Too many requests. Try again later."

    }

}
