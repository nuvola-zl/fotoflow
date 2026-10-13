package com.ff.utils;

import com.ff.exception.DurationTimeException;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class VideoDurationUtil {

    // 1. 上传文件时获取时长（同步）
    public static int getDurationSeconds(MultipartFile file) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("video_", ".tmp");
            try (InputStream in = file.getInputStream();
                 FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            }

            MultimediaObject multimediaObject = new MultimediaObject(tempFile);
            MultimediaInfo info = multimediaObject.getInfo();
            long durationMs = info.getDuration();
            return (int) (durationMs / 1000);

        } catch (Exception e) {
            // 抛出自定义异常
            throw new DurationTimeException("视频时长解析失败");
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    // 2. 根据URL获取时长（异步用 → 统一抛自定义异常！）
    public static int getDurationSeconds(String videoUrl) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("video_", ".tmp");

            try (InputStream in = new URL(videoUrl).openStream();
                 FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            }

            MultimediaObject multimediaObject = new MultimediaObject(tempFile);
            MultimediaInfo info = multimediaObject.getInfo();
            long durationMs = info.getDuration();
            return (int) (durationMs / 1000);

        } catch (Exception e) {
            // 统一抛自定义异常！！！
            throw new DurationTimeException("视频时长解析失败，URL：" + videoUrl);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}