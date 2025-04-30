/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.configuration.database

import com.fasterxml.jackson.databind.ObjectMapper
import com.tomaszwnuk.opencalendar.utility.logger.info
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

/**
 * Configuration class for Redis caching.
 * This class sets up the Redis template and cache manager for the application.
 */
@Suppress("unused")
@Configuration
@EnableCaching
class RedisConfiguration(
    /**
     * The `ObjectMapper` used for serializing and deserializing Redis cache values.
     */
    @Qualifier("redisObjectMapper")
    private val _objectMapper: ObjectMapper
) {

    /**
     * Configures the `RedisTemplate` bean for interacting with Redis.
     *
     * @param redisConnectionFactory The factory for creating Redis connections.
     *
     * @return The configured `RedisTemplate` instance.
     */
    @Bean
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = redisConnectionFactory

        val serializer = GenericJackson2JsonRedisSerializer(_objectMapper)
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = serializer
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = serializer
        template.afterPropertiesSet()

        return template
    }

    /**
     * Configures the `CacheManager` bean for managing application caches.
     * Falls back to an in-memory cache if Redis is unavailable.
     *
     * @param redisConnectionFactory The factory for creating Redis connections.
     *
     * @return The configured `CacheManager` instance.
     */
    @Bean
    fun cacheManager(redisConnectionFactory: RedisConnectionFactory): CacheManager {
        return try {
            val redisSerializer = GenericJackson2JsonRedisSerializer(_objectMapper)
            val configuration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))

            redisConnectionFactory.connection.ping()

            RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(configuration)
                .build()
                .also {
                    info(this, "Redis cache manager initialized successfully.")
                }
        } catch (exception: Exception) {
            info(this, "Redis cache manager is not available. Falling back to in-memory cache")
            ConcurrentMapCacheManager()
        }
    }

}
