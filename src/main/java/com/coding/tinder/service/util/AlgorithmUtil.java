package com.coding.tinder.service.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author samuel
 * @version 1.0
 * @project user-center
 * @description 算法工具类
 * @ClassName AlgorithmUtil
 */
public class AlgorithmUtil {


    /**
     * 编剧距离算法，计算两个字符串的相似度，值越小相似度越高
     * 原理： https://blog.csdn.net/DBC_121/article/details/104198838
     *
     * @param word1
     * @param word2
     * @return
     */
    public static int minDistance(String word1, String word2) {
        int n = word1.length();
        int m = word2.length();

        if (n * m == 0) {
            return n + m;
        }
        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i < n + 1; i++) {
            d[i][0] = i;
        }

        for (int j = 0; j < m + 1; j++) {
            d[0][j] = j;
        }

        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < m + 1; j++) {
                int left = d[i - 1][j] + 1;
                int down = d[i][j - 1] + 1;
                int leftDown = d[i - 1][j - 1];
                if (word1.charAt(i - 1) != word2.charAt(j - 1)) {
                    leftDown += 1;
                }
                d[i][j] = Math.min(left, Math.min(down, leftDown));
            }
        }
        return d[n][m];
    }


    /**
     * 计算两组标签的相似度，返回值越小越相似
     *
     * @param tags1 标签列表
     * @param tags2 标签列表
     * @return 相似度
     */
    public static long minDistanceTags(List<String> tags1, List<String> tags2) {
        int n = tags1.size();
        int m = tags2.size();

        if (n * m == 0) {
            return n + m;
        }
        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i < n + 1; i++) {
            d[i][0] = i;
        }

        for (int j = 0; j < m + 1; j++) {
            d[0][j] = j;
        }

        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < m + 1; j++) {
                int left = d[i - 1][j] + 1;
                int down = d[i][j - 1] + 1;
                int leftDown = d[i - 1][j - 1];
                if (!Objects.equals(tags1.get(i - 1), tags2.get(j - 1))) {
                    leftDown += 1;
                }
                d[i][j] = Math.min(left, Math.min(down, leftDown));
            }
        }
        return d[n][m];
    }
}
