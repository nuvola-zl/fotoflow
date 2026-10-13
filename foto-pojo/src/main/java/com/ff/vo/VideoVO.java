package com.ff.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoVO {
    private Long id;             // 视频ID
    private Long userId;         // 发布者ID
    private String videoUrl;      // 视频地址
    private Integer duration;     // 时长
    private Integer likeCount;    // 点赞数
    private Integer commentCount; // 评论数
    private Integer collectCount; // 收藏数

    private Boolean isLike;       // 是否点赞
    private Boolean isCollect;    // 是否收藏

}
