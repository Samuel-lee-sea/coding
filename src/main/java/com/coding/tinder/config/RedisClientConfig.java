package com.coding.tinder.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author samuel
 * @version 1.0
 * @project user-center
 * @ClassName RedisClientConfig
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisClientConfig {

    private String host;

    private String port;

    public RedisClientConfig() {
    }

    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + host + ":" + port);
        return Redisson.create(config);
    }
}
