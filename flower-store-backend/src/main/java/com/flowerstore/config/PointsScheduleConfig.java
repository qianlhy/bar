package com.flowerstore.config;

import com.flowerstore.service.PointsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 每日 0 点清零用户积分
 */
@Configuration
@EnableScheduling
public class PointsScheduleConfig {

    private static final Logger log = LoggerFactory.getLogger(PointsScheduleConfig.class);

    @Autowired
    private PointsService pointsService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void resetPointsAtMidnight() {
        try {
            int count = pointsService.resetAllPoints();
            log.info("每日积分清零完成，影响用户数：{}", count);
        } catch (Exception e) {
            log.error("每日积分清零失败", e);
        }
    }
}
