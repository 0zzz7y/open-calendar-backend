/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.configuration.throttling

import com.tomaszwnuk.opencalendar.utility.throttling.ThrottlingFilter
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
