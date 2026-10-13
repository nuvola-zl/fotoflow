package org.example.fotoflow.service;

import org.example.fotoflow.pojo.*;

import java.util.List;

public interface VideoService {
    // 视频发布
    void publishVideo(Video video);

    // 获取首页最新一条视频
    Video getLatestOne();

    // 发送弹幕
    void sendBarrage(Barrage barrage);

    // 获取视频弹幕
    List<Barrage> getBarrages(Long videoId);

    // 判断用户是否点赞过视频
    Like likeIsExist(Long id, Long userId);

    // 更新视频点赞数
    void updateWorkLike(Long id, int i);

    // 更新用户点赞状态
    void updateLike(Long id, Long userId, int i);

    // 更新视频收藏数
    void updateVideoCollect(Long id, int i);

    // 更新用户收藏状态
    void updateMyCollect(Long id, Long userId, int i);

    // 判断用户是否收藏过视频
    Collect collectIsExist(Long id, Long userId);

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
}
