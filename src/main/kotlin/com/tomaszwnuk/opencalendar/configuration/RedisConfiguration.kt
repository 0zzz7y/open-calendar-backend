package com.tomaszwnuk.opencalendar.configuration

import com.tomaszwnuk.opencalendar.utility.info
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import java.time.Duration

@Suppress("unused")
@Configuration
@EnableCaching
class RedisConfiguration {

    @Bean
    fun cacheManager(redisConnectionFactory: RedisConnectionFactory): CacheManager {
        return try {
            val configuration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))

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
