package com.tomaszwnuk.opencalendar.throttling

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Configuration

@Suppress("unused")
@Configuration
class ThrottlingFilterConfiguration {

    fun rateLimitingFilter(filter: ThrottlingFilter): FilterRegistrationBean<ThrottlingFilter> {
        val registration: FilterRegistrationBean<ThrottlingFilter> = FilterRegistrationBean<ThrottlingFilter>(filter)
        registration.order = 1
        registration.addUrlPatterns("/*")
        return registration
    }

}
