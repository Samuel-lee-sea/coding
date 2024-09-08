package com.coding.tinder.usercenter.service;

import com.coding.tinder.service.util.AlgorithmUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author samuel
 * @version 1.0
 * @project user-center
 * @description 测试算法工具类
 * @ClassName AlgorithmUtilTest
 */
public class AlgorithmUtilTest {

    @Test
    void matchStr(){
        String str1 = "鱼皮是dog";
        String str2 = "鱼皮就是dog";
        String str3 = "鱼皮可能是dog";
        int score1 = AlgorithmUtil.minDistance(str1, str2);
        int score2 = AlgorithmUtil.minDistance(str1, str3);
        System.out.println(score1);
        System.out.println(score2);
    }

    @Test
    void matchTags(){
        List<String> tags1 = Arrays.asList("java","男","大一","篮球");
        List<String> tags2 = Arrays.asList("java","男","大二","篮球");
        List<String> tags3 = Arrays.asList("java","男","大三","rap");
        long score1 = AlgorithmUtil.minDistanceTags(tags1, tags2);
        long score2 = AlgorithmUtil.minDistanceTags(tags1, tags3);
        System.out.println(score1);
        System.out.println(score2);
    }
}
