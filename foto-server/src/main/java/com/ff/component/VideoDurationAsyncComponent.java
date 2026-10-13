package com.ff.component;

import com.ff.entity.Video;
import com.ff.exception.DurationTimeException;
import com.ff.mapper.VideoMapper;
import com.ff.utils.VideoDurationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VideoDurationAsyncComponent {

    private final VideoMapper videoMapper;

    @Async("videoExecutor")
    public void asyncResolveDuration(Video video) {
        try {
            log.info("开始解析视频时长, videoId={}", video.getId());
            int duration = VideoDurationUtil.getDurationSeconds(video.getVideoUrl());
            video.setDuration(duration);
            video.setStatus("SUCCESS");  // 成功
            videoMapper.updateById(video);
            log.info("视频时长解析成功, videoId={}, duration={}s", video.getId(), duration);
        } catch (DurationTimeException e) {
            log.error("视频时长解析失败, videoId={}, 原因: {}", video.getId(), e.getMessage());
            // 记录失败状态，后续可定时重试
            videoMapper.updateStatus(video.getId(), "DURATION_FAILED");
        } catch (Exception e) {
            log.error("视频处理异常, videoId={}", video.getId(), e);
            videoMapper.updateStatus(video.getId(), "DURATION_FAILED");
        }
    }
}