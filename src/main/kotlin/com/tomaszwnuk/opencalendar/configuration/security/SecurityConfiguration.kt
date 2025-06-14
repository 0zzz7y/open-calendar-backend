package com.tomaszwnuk.opencalendar.configuration.security

import com.tomaszwnuk.opencalendar.domain.communication.CommunicationConstants
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Suppress("unused")
@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val _securityAuthenticationFilter: SecurityAuthenticationFilter
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            cors { }
            csrf { disable() }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            authorizeHttpRequests {
                authorize("/", permitAll)
                authorize(INDEX_HTML_URL, permitAll)
                authorize(STATIC_RESOURCES_URL, permitAll)

                authorize(SWAGGER_UI_URL, permitAll)
                authorize(SWAGGER_UI_HTML_URL, permitAll)
                authorize(API_DOCUMENTATION_URL, permitAll)

                authorize(AUTHENTICATION_URL_PATTERN, permitAll)
                authorize(anyRequest, authenticated)
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(_securityAuthenticationFilter)
        }
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf(
            CommunicationConstants.FRONTEND_DEVELOPMENT_URL,
            CommunicationConstants.FRONTEND_PRODUCTION_URL
        )
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)

        return source
    }

    companion object {

        const val AUTHENTICATION_URL_PATTERN: String = "/${CommunicationConstants.API}/${CommunicationConstants.API_VERSION}/authentication/**"

        const val INDEX_HTML_URL: String = "/index.html"

        const val STATIC_RESOURCES_URL: String = "/static/**"

        const val SWAGGER_UI_URL: String = "/swagger-ui/**"

        const val SWAGGER_UI_HTML_URL: String = "/swagger-ui.html"

        const val API_DOCUMENTATION_URL: String = "/v3/api-docs/**"
    }

}
