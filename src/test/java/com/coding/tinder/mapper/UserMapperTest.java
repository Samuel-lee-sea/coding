package com.coding.tinder.mapper;


import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;


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
