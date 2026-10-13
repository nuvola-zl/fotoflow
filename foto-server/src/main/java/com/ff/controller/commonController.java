package com.ff.controller;

import com.ff.exception.BaseException;
import com.ff.exception.CodeException;
import com.ff.result.Result;
import com.ff.utils.AliOssUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/common")
@RequiredArgsConstructor
public class CommonController {

    private final AliOssUtil aliOssUtil;

    /** 允许上传的文件后缀白名单 */
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp",  // 图片
            "mp4", "mov", "avi", "webm",           // 视频
            "mp3", "wav", "aac"                     // 音频（按需加）
    );

    /** 单个文件最大 50MB */
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        // 1. 空文件校验
        if (file == null || file.isEmpty()) {
            throw new BaseException(CodeException.PARAM_ERROR, "上传文件不能为空");
        }

        // 2. 文件大小校验
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BaseException(CodeException.PARAM_ERROR, "文件大小不能超过50MB");
        }

        // 3. 原始文件名处理
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new BaseException(CodeException.PARAM_ERROR, "文件名不能为空");
        }

        // 4. 安全提取后缀
        String extension = extractExtension(originalFilename).toLowerCase();

        // 5. 后缀白名单校验
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BaseException(CodeException.PARAM_ERROR, "不支持的文件类型");
        }

        // 6. 构造新文件名：uuid + 后缀
        String objectName = UUID.randomUUID().toString().replace("-", "") + "." + extension;

        try {
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            log.info("文件上传成功: {}", objectName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BaseException(CodeException.GENERAL, "文件上传失败，请重试");
        }
    }

    /**
     * 安全提取文件后缀（无后缀返回空字符串，不会抛异常）
     */
    private String extractExtension(String filename) {
        int lastDot = filename.lastIndexOf(".");
        if (lastDot == -1 || lastDot == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDot + 1);
    }
}