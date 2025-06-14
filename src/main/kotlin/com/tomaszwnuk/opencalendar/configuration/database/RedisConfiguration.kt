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

@Suppress("unused")
@Configuration
@EnableCaching
class RedisConfiguration(
    @Qualifier("redisObjectMapper")
    private val _objectMapper: ObjectMapper
) {

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

        private const val REDIS_CACHE_TTL_IN_MINUTES: Long = 30L

    }

}
