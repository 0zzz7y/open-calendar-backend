package com.tomaszwnuk.opencalendar.configuration.throttling

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Configuration

@Suppress("unused")
@Configuration
class ThrottlingFilterConfiguration {

    fun rateLimitingFilter(filter: ThrottlingFilter): FilterRegistrationBean<ThrottlingFilter> {
        val registration: FilterRegistrationBean<ThrottlingFilter> = FilterRegistrationBean<ThrottlingFilter>(filter)
        registration.order = 1
        registration.addUrlPatterns(THROTTLING_FILTER_URL_PATTERN)
        return registration
    }

    companion object {

        const val THROTTLING_FILTER_URL_PATTERN: String = "/*"
    }

}
