package com.ff.dto;

import lombok.Data;

/**
 * 商品查询参数（分页）
 * 商家端和用户端共用，通过接口路径区分查询范围
 */
@Data
public class GoodsQueryDto {
    /** 当前页码，默认1 */
    private Integer pageNum = 1;
    /** 每页条数，默认10 */
    private Integer pageSize = 10;

    /** 商品名称（模糊搜索） */
    private String goodsName;
    /** 状态筛选：0下架 1上架（商家端可用） */
    private Integer status;
    /** 店铺配置ID（用户端可按店铺筛选） */
    private Long shopConfigId;
}