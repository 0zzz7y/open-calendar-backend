package com.tomaszwnuk.dailyassistant.configuration

import com.tomaszwnuk.dailyassistant.validation.RateLimitingFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Configuration

@Suppress("unused")
@Configuration
class RateLimitingFilterConfiguration {

    fun rateLimitingFilter(filter: RateLimitingFilter): FilterRegistrationBean<RateLimitingFilter> {
        val registration: FilterRegistrationBean<RateLimitingFilter> = FilterRegistrationBean<RateLimitingFilter>(filter)
        registration.order = 1
        registration.addUrlPatterns("/*")
        return registration
    }

}
