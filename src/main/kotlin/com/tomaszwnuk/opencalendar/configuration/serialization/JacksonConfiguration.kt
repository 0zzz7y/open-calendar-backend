package com.tomaszwnuk.opencalendar.configuration.serialization

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Suppress("unused")
@Configuration
class JacksonConfiguration {

    @Bean
    @Primary
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
            .registerModule(JavaTimeModule())
            .registerModule(com.fasterxml.jackson.module.kotlin.kotlinModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @Bean(name = ["redisObjectMapper"])
    fun redisObjectMapper(): ObjectMapper {
        return ObjectMapper()
            .registerModule(JavaTimeModule())
            .registerModule(com.fasterxml.jackson.module.kotlin.kotlinModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setVisibility(
                PropertyAccessor.ALL,
                JsonAutoDetect.Visibility.ANY
            )
            .also {
                it.activateDefaultTyping(
                    LaissezFaireSubTypeValidator.instance,
                    ObjectMapper.DefaultTyping.NON_FINAL
                )
                it.setVisibility(
                    PropertyAccessor.ALL,
                    JsonAutoDetect.Visibility.ANY)
            }
    }

}
