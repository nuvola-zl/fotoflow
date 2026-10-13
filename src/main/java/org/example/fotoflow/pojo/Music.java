package org.example.fotoflow.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Music {
    private Long id;
    private String name;
    private String singer;
    private String url;
    private String cover;
    private Integer duration;
    private Long playCount;
    private LocalDateTime createTime;
}
