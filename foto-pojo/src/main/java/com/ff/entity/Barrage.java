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
public class Barrage {
    private Long id;
    private Long videoId;
    private Long userId;
    private String content;
    private String color;
    private Double videoTime;
    private LocalDateTime createTime;
}
