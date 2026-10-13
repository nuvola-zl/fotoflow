package org.example.fotoflow.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Message {
    private Long id;
    private Long fromUserId;  // 发送人
    private Long toUserId;    // 接收人
    private String content;   // 内容
    private Integer isRead;   // 0未读 1已读
    private LocalDateTime createTime;

    //private String avatarUrl;  //附加，1.发送人头像，2.接收人头像
}
