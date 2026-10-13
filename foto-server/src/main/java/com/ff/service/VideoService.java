package com.ff.service;

import com.ff.dto.VideoPublishDto;
import com.ff.entity.*;
import com.ff.vo.BarrageVO;
import com.ff.vo.VideoVO;


import java.util.List;

public interface VideoService {
    // 视频发布
    void publishVideo(VideoPublishDto videoPublishDto);

    // 获取首页最新一条视频
    VideoVO getLatestOne();

    // 发送弹幕
    void sendBarrage(Barrage barrage,Long videoId);

    // 获取视频弹幕
    List<BarrageVO> getBarrages(Long videoId);

    Boolean updateLikeAll(Long id);


    Boolean updateCollectAll(Long id);


    // 获取视频评论
    List<Comment> listCommentsByWorkId(Long id);

    // 添加评论
    void addComment(Comment comment);

    // 更新视频评论数
    void addCommentNum(Long id);

    //根据作品id查询作者
    Video getVideoById(Long id);

    //根据用户名查询用户ID
    User getUserByUsername(String username);

    //添加信息
    void addAtMessage(Message msg);

   //异步处理视频时长
    void updateById(Video video);
}
