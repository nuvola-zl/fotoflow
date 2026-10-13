package com.ff.service;

import com.ff.dto.RiderLocationDto;
import com.ff.vo.RiderLocationVO;

import java.util.List;

/**
 * 骑手位置服务
 */
public interface RiderLocationService {

    /**
     * 上报实时位置
     * 骑手端每5-10秒调用一次
     */
    void uploadLocation(RiderLocationDto dto);

    /**
     * 查询骑手最新位置（用户端查看）
     */
    RiderLocationVO getLatestLocation(String orderNo);

    /**
     * 查询配送完整轨迹（画路线）
     */
    List<RiderLocationVO> getDeliveryPath(String orderNo);
}