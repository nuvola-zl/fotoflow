package org.example.fotoflow.pojo;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class Barrage {
    private Long id;
    private Long videoId;
    private Long userId;
    private String content;
    private String color;
    private Double videoTime;
    private LocalDateTime createTime;
}
