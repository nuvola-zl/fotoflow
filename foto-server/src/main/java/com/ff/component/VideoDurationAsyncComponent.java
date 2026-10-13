package com.ff.component;

import com.ff.entity.Video;
import com.ff.exception.DurationTimeException;
import com.ff.mapper.VideoMapper;
import com.ff.service.VideoService;
import com.ff.utils.VideoDurationUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class VideoDurationAsyncComponent {

    private final BlockingQueue<Video> videoQueue = new ArrayBlockingQueue<>(100);

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            1, 1,
            0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1),
            r -> new Thread(r, "video-duration-handler")
    );

    // 就用这个，绝对能成
    @Autowired
    private VideoMapper videoMapper;


    @PostConstruct
    public void startAsyncTask() {
        executor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Video video = null;
                try {
                    // 从队列取任务（阻塞方法）
                    video = videoQueue.take();

                    // 解析时长
                    int duration = VideoDurationUtil.getDurationSeconds(video.getVideoUrl());
                    video.setDuration(duration);
                    videoMapper.updateById(video);

                } catch (DurationTimeException e) {
                    // 业务异常：视频解析失败
                    if (video != null) {
                        System.err.println("视频解析失败 id=" + video.getId() + "，原因：" + e.getMessage());
                    }
                } catch (InterruptedException e) {
                    // 【关键】线程被中断 → 退出循环
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    // 其他未知异常
                    if (video != null) {
                        System.err.println("视频处理失败 id=" + video.getId());
                    }
                }
            }
        });
    }
    public void asyncResolveDuration(Video video) {
        try {
            videoQueue.put(video);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}