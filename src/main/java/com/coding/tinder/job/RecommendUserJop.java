package com.coding.tinder.job;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coding.tinder.mapper.UserMapper;
import com.coding.tinder.model.domain.User;
import com.coding.tinder.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.coding.tinder.constant.UserConstant.RECOMMEND_USER;


/**
 * @author samuel
 * @version 1.0
 * @project user-center
 * @description RecommendUserJop
 * @ClassName RecommendUserJop
 */
@Component
@Slf4j
public class RecommendUserJop {

    private final Integer[] userIds = {4, 5, 6, 7};

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 每日24点更新
     */
    @Scheduled(cron = "0 58 23 * * ?")
    public void recommendUser() {
        RLock lock = redissonClient.getLock("yupao:user:recommend");
        try {
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                log.info("start rows:{}", userIds.length);
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                List<User> users = userMapper.selectBatchIds(Arrays.asList(userIds));
                for (int i = 0; i < users.size(); i++) {
                    try {
                        User user = users.get(i);
                        String tagsStr = user.getTags();
                        List<String> tags = JSONUtil.toList(tagsStr, String.class);
                        queryWrapper.clear();
                        tags.forEach(item -> queryWrapper.like("tags", item));
                        Page<User> userPage = userMapper.selectPage(new Page<>(1, 8), queryWrapper);
                        String recommendKey = String.format(RECOMMEND_USER + "%s", user.getId());
                        redisTemplate.opsForValue().set(recommendKey, JSONUtil.toJsonStr(userPage), 1, TimeUnit.DAYS);
                    } catch (Exception e) {
                        log.error("jop recommend error:}", e);
                    }
                }
                stopWatch.stop();
                log.info("sucess time cost:{}", stopWatch.getTotalTimeMillis());
            }
        } catch (InterruptedException e) {
            log.error("user-recommend job error: " + e.getMessage());
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
