package com.ff.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BarrageVO {
    private Long id;             // 弹幕ID
    private Long videoId;        // 视频ID
    private String content;       // 弹幕内容
    private String color;         // 颜色
}
