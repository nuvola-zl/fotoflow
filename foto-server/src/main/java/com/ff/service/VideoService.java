package com.ff.service;

import com.ff.dto.VideoPublishDto;
import com.ff.entity.*;
import com.ff.vo.BarrageVO;
import com.ff.vo.VideoVO;

import java.util.List;

/**
 * 视频业务接口
 * 功能：视频发布、点赞、收藏、弹幕、首页推荐等
 */
public interface VideoService {

    /**
     * 发布视频
     * @param videoPublishDto 视频发布参数（标题、内容、视频地址等）
     */
    void publishVideo(VideoPublishDto videoPublishDto);

    /**
     * 获取首页【最新/随机】一条视频
     * 用于首页刷视频展示
     * @return VideoVO 视频详情（包含是否点赞、是否收藏）
     */
    VideoVO getLatestOne();

    /**
     * 发送弹幕
     * @param barrage 弹幕内容、颜色、出现时间等信息
     * @param videoId 视频ID
     */
    void sendBarrage(Barrage barrage, Long videoId);

    /**
     * 获取某个视频的所有弹幕列表
     * @param videoId 视频ID
     * @return 弹幕VO列表，返回给前端
     */
    List<BarrageVO> getBarrages(Long videoId);

    /**
     * 点赞 / 取消点赞（二合一）
     * @param id 视频ID
     * @return true=点赞成功 false=取消点赞成功
     */
    Boolean updateLikeAll(Long id);

    /**
     * 收藏 / 取消收藏（二合一）
     * @param id 视频ID
     * @return true=收藏成功 false=取消收藏成功
     */
    Boolean updateCollectAll(Long id);

}