package org.example.fotoflow.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Collect {
    private Long id;
    private Long userId;
    private Long workId;
    private LocalDateTime createTime;
}
