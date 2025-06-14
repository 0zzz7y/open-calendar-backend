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
 * Configuration for Redis database and caching.
 */
@Suppress("unused")
@Configuration
@EnableCaching
class RedisConfiguration(

    /**
     * The object mapper used for serializing and deserializing objects in Redis.
     */
    @Qualifier("redisObjectMapper")
    private val _objectMapper: ObjectMapper
) {

    /**
     * Creates a Redis template for interacting with Redis.
     *
     * @param redisConnectionFactory The factory for creating Redis connections
     *
     * @return A configured Redis template
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
     * Creates a cache manager for managing Redis caches.
     *
     * @param redisConnectionFactory The factory for creating Redis connections
     *
     * @return A configured cache manager
     */
    @Bean
    fun cacheManager(redisConnectionFactory: RedisConnectionFactory): CacheManager {
        return try {
            val redisSerializer = GenericJackson2JsonRedisSerializer(_objectMapper)
            val configuration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(REDIS_CACHE_TTL_IN_MINUTES))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))

            redisConnectionFactory.connection.ping()

            RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(configuration)
                .build()
                .also { info(source = this, message = "Redis cache manager initialized successfully.") }
        } catch (exception: Exception) {
            info(source = this, message = "Redis cache manager is not available. Falling back to in-memory cache.")
            ConcurrentMapCacheManager()
        }
    }

    companion object {

        /**
         * The time-to-live for Redis cache entries in minutes.
         */
        private const val REDIS_CACHE_TTL_IN_MINUTES: Long = 30L

    }

}
