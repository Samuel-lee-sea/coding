package com.coding.tinder.usercenter.service;

import java.util.*;


import com.coding.tinder.mapper.UserMapper;
import com.coding.tinder.model.domain.User;
import com.coding.tinder.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author samuel
 * @version 1.0
 * @project user-center
 * @description 测试类
 * @ClassName UserServiceTest
 */
@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;

    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(20, 40, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10000));


    @Test
    void addUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int addNum = 1000;
        int number = 10;
        int i = 0;
        List<CompletableFuture> futures = Collections.synchronizedList(new ArrayList<>());
        for (int j = 0; j < number; j++) {
            ArrayList<User> list = new ArrayList<>();
            while (true) {
                i++;
                User user = new User();
                user.setUsername("假数据");
                user.setProfile("唱跳rap");
                user.setAvatarUrl("http://i.heyige.cn/images/logo.png");
                user.setUserAccount("100");
                user.setUserPassword("666");
                user.setGender(0);
                user.setPhone("156465644564");
                user.setEmail("5465@qq.com");
                user.setUserStatus(0);
                user.setTags("");
                user.setCreateTime(new Date());
                user.setUpdateTime(new Date());
                user.setIsDelete(0);
                user.setUserRole(0);
                user.setPlanetCode("000");
                list.add(user);

                if (i % addNum == 0) {
                    break;
                }
            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("当前线程: " + Thread.currentThread().getName());
                userService.saveBatch(list);
            }, threadPoolExecutor);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    @Test
    void searchUserByTag() {
        List<String> strings = Arrays.asList("java", "python");
        List<User> users = userService.searchUserByTag(strings);
        System.out.println(users.toString());
    }

    @Test
    void addUser() {
        User user = new User();
        user.setUsername("mi");
        user.setUserPassword("12345");
        boolean save = userService.save(user);
        Assertions.assertTrue(save);
        System.out.println(user.getId());
    }

    @Test
    void register() {
        String userAccount = "User2";
        String password = "123456789";
        String code = "123456789";
        String planetCode = "1";
        long l = userService.userRegister(userAccount, password, code, planetCode);
        System.out.println(l);
//        long l = userService.userRegister(userAccount, password, code);
//        Assertions.assertEquals(-1,l);
//        userAccount = "miUser";
//        long l1 = userService.userRegister(userAccount, password, code);
//        Assertions.assertEquals(-1,l1);
//        password = "12345678";
//        long l2 = userService.userRegister(userAccount, password, code);
//        Assertions.assertEquals(-1,l2);
//        userAccount = "mi bi";
//        code = "12345678";
//        long l3 = userService.userRegister(userAccount, password, code);
//        Assertions.assertEquals(-1,l3);
//        userAccount = "miUser";
//        long l4 = userService.userRegister(userAccount, password, code);
//        System.out.println(l4);

    }
}
