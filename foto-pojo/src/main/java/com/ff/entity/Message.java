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
public class Message {
    private Long id;
    private Long fromUserId;  // 发送人
    private Long toUserId;    // 接收人
    private String content;   // 内容
    private Integer isRead;   // 0未读 1已读
    private LocalDateTime createTime;

    //private String avatarUrl;  //附加，1.发送人头像，2.接收人头像
}
