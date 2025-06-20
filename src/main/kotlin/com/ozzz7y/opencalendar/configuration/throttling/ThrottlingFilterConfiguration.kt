package com.ozzz7y.opencalendar.configuration.throttling

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Configuration

/**
 * The configuration for the throttling filter.
 */
@Suppress("unused")
@Configuration
class ThrottlingFilterConfiguration {

    /**
     * Registers the throttling filter with the specified URL pattern.
     *
     * @param filter The throttling filter to register
     *
     * @return The filter registration bean for the throttling filter
     */
    fun rateLimitingFilter(filter: ThrottlingFilter): FilterRegistrationBean<ThrottlingFilter> {
        val registration: FilterRegistrationBean<ThrottlingFilter> = FilterRegistrationBean<ThrottlingFilter>(filter)
        registration.order = 1
        registration.addUrlPatterns(THROTTLING_FILTER_URL_PATTERN)
        return registration
    }

    companion object {

        /**
         * The URL pattern for the throttling filter.
         */
        const val THROTTLING_FILTER_URL_PATTERN: String = "/*"
    }

}
