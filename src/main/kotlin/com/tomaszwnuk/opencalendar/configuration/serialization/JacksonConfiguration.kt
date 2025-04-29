package com.tomaszwnuk.opencalendar.configuration.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Suppress("unused")
@Configuration
class JacksonConfiguration {

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
            .registerModule(JavaTimeModule())
            .registerModule(com.fasterxml.jackson.module.kotlin.kotlinModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

}
