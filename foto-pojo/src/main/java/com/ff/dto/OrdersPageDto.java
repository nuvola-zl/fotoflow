package com.ff.dto;
import lombok.Data;
@Data
public class OrdersPageDto {
    private Integer page;      // 页码
    private Integer pageSize;  // 每页条数
    private String orderNo;  // 搜索条件（可选）
    private Integer status;    // 状态（可选）
}