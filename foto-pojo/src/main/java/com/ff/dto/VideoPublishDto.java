package com.ff.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class VideoPublishDto {
    private String title;
    private String content;
    private MultipartFile videoUrl;
}
