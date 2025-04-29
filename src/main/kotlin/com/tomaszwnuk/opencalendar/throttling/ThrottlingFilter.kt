package com.tomaszwnuk.opencalendar.throttling

import com.tomaszwnuk.opencalendar.communication.CommunicationConstraints.MAXIMUM_REQUESTS_PER_MINUTE
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
import java.util.concurrent.ConcurrentHashMap
import java.time.Duration

@Component
class ThrottlingFilter : Filter {

    private val _buckets: MutableMap<String, Bucket> = ConcurrentHashMap()

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse
        val clientIp = getClientIp(httpRequest)
        val bucket = _buckets.computeIfAbsent(clientIp) { createBucket() }

        if (bucket.tryConsume(1)) {
            chain?.doFilter(request, response)
        } else {
            httpResponse.status = 429
            httpResponse.writer.write("Too many requests. Try again later.")
        }
    }

    private fun createBucket(): Bucket {
        val limit = Bandwidth.classic(MAXIMUM_REQUESTS_PER_MINUTE, Refill.greedy(MAXIMUM_REQUESTS_PER_MINUTE, Duration.ofMinutes(1)))
        return Bucket.builder().addLimit(limit).build()
    }

    private fun getClientIp(request: HttpServletRequest): String {
        return request.getHeader("X-Forwarded-For") ?: request.remoteAddr
    }

}
