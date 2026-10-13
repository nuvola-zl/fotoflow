package com.ff.job;

import com.ff.entity.Video;
import com.ff.mapper.VideoMapper;
import com.ff.component.VideoDurationAsyncComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class VideoRetryJob {


    private final  VideoMapper videoMapper;

    @Autowired
    private VideoDurationAsyncComponent durationComponent;

    // 每 5 分钟扫描一次失败任务
    @Scheduled(fixedRate = 300000)
    public void retryFailedVideos() {
        List<Video> failedVideos = videoMapper.findFailedDurationTasks();
        if (failedVideos.isEmpty()) return;

        log.info("发现 {} 个视频时长解析失败，开始重试", failedVideos.size());
        for (Video video : failedVideos) {
            durationComponent.asyncResolveDuration(video);
        }
    }
}
