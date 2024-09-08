package com.coding.tinder.usercenter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author samuel
 * @version 1.0
 * @project user-center
 * @description 测试redis连接
 * @ClassName RedisTemplateTest
 */
@SpringBootTest
public class RedisTemplateTest {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void test(){
        redisTemplate.opsForValue().set("k1","dogMI");
        String k1 = (String) redisTemplate.opsForValue().get("k1");
        Assertions.assertEquals("dogMI", k1);

    }

}
