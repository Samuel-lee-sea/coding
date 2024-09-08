package com.coding.tinder.config;

import io.lettuce.core.dynamic.RedisCommandFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author samuel
 * @version 1.0
 * @project user-center
 * @description redisTemplate
 * @ClassName RedisTemplateConfig
 */
@Configuration
public class RedisTemplateConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setDefaultSerializer(RedisSerializer.string());
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }
}
