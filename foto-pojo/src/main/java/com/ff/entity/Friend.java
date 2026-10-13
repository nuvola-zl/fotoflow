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
public class Friend {
    private Long id;
    private Long friendId;
    private Long userId;
    private String remark;
    private Integer status;
    private LocalDateTime createTime;
}
