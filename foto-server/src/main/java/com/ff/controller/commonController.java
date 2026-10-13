package com.ff.controller;
import com.ff.result.Result;

import com.ff.utils.AliOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */

@RestController
@RequestMapping("/admin/common")

public class commonController {

    @Autowired
    private AliOssUtil aliOssUtil;
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file)
    {
        try {
            //原始文件名
            String originalFilename=file.getOriginalFilename();

            //截取文件名后缀
            String extension= null;
            if (originalFilename != null) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            //构造新文件名
            String objectName=UUID.randomUUID().toString()+extension;

            String filePath=aliOssUtil.upload(file.getBytes(),objectName);

            return Result.success(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}