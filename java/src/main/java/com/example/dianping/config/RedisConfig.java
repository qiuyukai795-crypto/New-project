package com.example.dianping.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer();

        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                // Use a versioned prefix so stale cache entries from older serializers are ignored.
                .computePrefixWith(cacheName -> "dianping:v5:" + cacheName + "::")
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues();
    }
}
