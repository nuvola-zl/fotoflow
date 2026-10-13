package com.ff.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Video {
    private Long id;
    private Long userId;          // 发布者ID
    private Long goodsId;        // 商品ID
    private String title;         // 标题
    private String content;       // 描述
    private String videoUrl;      // 视频地址
    private String coverUrl;      // 封面地址
    private Integer duration;     // 时长

    private Integer state;        // 0待审核 1通过 2拒绝
    private String rejectReason;  // 驳回原因

    private Integer likeCount;    // 点赞数
    private Integer commentCount; // 评论数
    private Integer collectCount; // 收藏数

    private Boolean isLike;       // 是否点赞  附加
    private Boolean isCollect;    // 是否收藏  附加

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String status = "PENDING";  // 新字段，默认 PENDING,解析视频时长是否成功状态
}
