package org.example.fotoflow.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long workId;//关联作品ID
    private Long userId;//评论人ID
    private String content;//评论内容
    private LocalDateTime createTime;       //评论时间
    private LocalDateTime updateTime;       //更新时间

    private Long videoId;//关联视频ID

    private String username;//评论人用户名
    // 这个可以改过用户数据表之后添加评论人头像
    private String avatar;//评论人头像

}
