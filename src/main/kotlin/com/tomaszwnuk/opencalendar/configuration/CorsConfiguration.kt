package com.tomaszwnuk.opencalendar.configuration

import com.tomaszwnuk.opencalendar.communication.CommunicationConstants.FRONTEND_DEVELOPMENT_URL
import com.tomaszwnuk.opencalendar.communication.CommunicationConstants.FRONTEND_PRODUCTION_URL
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Suppress("unused")
@Configuration
class CorsConfiguration : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(FRONTEND_PRODUCTION_URL, FRONTEND_DEVELOPMENT_URL)
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
    }

}
