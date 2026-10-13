package com.ff.task;

import com.ff.mapper.RiderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 骑手每日统计清零
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RiderDailyResetTask {

    private final RiderMapper riderMapper;

    @Scheduled(cron = "0 0 0 * * ?") // 每天0点
    public void resetTodayOrders() {
        riderMapper.resetTodayOrders();
        log.info("骑手今日订单数已清零");
    }
}