package com.coding.tinder.usercenter.mapper;


import com.coding.tinder.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;


@SpringBootTest
@RunWith(SpringRunner.class)
class UserMapperTest {

    @Resource
    private UserMapper userMapper;

    @Test
    public void test(){
       try {
           int i = 0 %1;
       }catch (Exception e){
           System.out.println(e.getMessage());
       }
    }

}
