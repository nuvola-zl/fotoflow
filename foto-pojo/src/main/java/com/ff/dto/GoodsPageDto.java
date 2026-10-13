package com.ff.dto;

import lombok.Data;


import lombok.Data;

@Data
public class GoodsPageDto {
    private Integer page;      // 页码
    private Integer pageSize;  // 每页条数
    private String goodsName;  // 搜索条件（可选）
    private Integer status;    // 状态（可选）
}