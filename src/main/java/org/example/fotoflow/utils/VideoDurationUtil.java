package org.example.fotoflow.utils;

import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class VideoDurationUtil {

    // 获取视频时长，返回 秒
    public static int getDurationSeconds(MultipartFile file) throws Exception {
        File tempFile = null;
        try {
            // 1. 创建临时文件
            tempFile = File.createTempFile("video_", ".tmp");
            try (InputStream in = file.getInputStream();
                 FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            }

            // 2. 获取时长
            MultimediaObject multimediaObject = new MultimediaObject(tempFile);
            MultimediaInfo info = multimediaObject.getInfo();
            long durationMs = info.getDuration();
            return (int) (durationMs / 1000);

        } finally {
            // 3. 删除临时文件
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}