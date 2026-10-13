package org.example.fotoflow.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Friend {
    private Long id;
    private Long friendId;
    private Long userId;
    private String remark;
    private Integer status;
    private LocalDateTime createTime;
}
