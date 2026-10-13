package org.example.fotoflow.service.impl;

import jakarta.annotation.Resource;
import org.example.fotoflow.mapper.VideoMapper;
import org.example.fotoflow.pojo.*;
import org.example.fotoflow.service.VideoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {
    @Resource
    private VideoMapper videoMapper;

    //发布视频
    @Override
    public void publishVideo(Video video) {
        videoMapper.publishVideo(video);
    }

    // 获取首页最新一条视频
    @Override
    public Video getLatestOne() {
        return videoMapper.getLatestOne();
    }

    // 发送弹幕
    @Override
    public void sendBarrage(Barrage barrage) {
        videoMapper.sendBarrage(barrage);
    }

    // 获取视频弹幕
    @Override
    public List<Barrage> getBarrages(Long videoId) {
        return videoMapper.getBarrages(videoId);
    }

    // 判断用户是否点赞过视频
    @Override
    public Like likeIsExist(Long id, Long userId) {
        return videoMapper.likeIsExist(id, userId);
    }

    // 更新视频点赞数
    @Override
    public void updateWorkLike(Long id, int i) {
        videoMapper.updateVideoLike(id, i);
    }

    // 更新用户点赞状态
    @Override
    public void updateLike(Long id, Long userId, int i) {
        if (i == 1) {
            videoMapper.updateMyLikeAdd(id, userId);
        } else {

            videoMapper.updateMyLikeSub(id, userId);
        }
    }

    // 更新视频收藏数
    @Override
    public void updateVideoCollect(Long id, int i) {
        videoMapper.updateVideoCollect(id, i);
    }

    // 更新用户收藏状态
    @Override
    public void updateMyCollect(Long id, Long userId, int i) {
        if (i == 1) {
            videoMapper.updateMyCollectAdd(id, userId);
        } else {

            videoMapper.updateMyCollectSub(id, userId);
        }
    }

    //判断用户是否收藏过该视频
    @Override
    public Collect collectIsExist(Long id, Long userId) {
        return videoMapper.collectIsExist(id, userId);
    }

    // 获取视频评论
    @Override
    public List<Comment> listCommentsByWorkId(Long id) {
        return videoMapper.listCommentsByWorkId(id);
    }

    // 添加评论
    @Override
    public void addComment(Comment comment) {
        videoMapper.addComment(comment);
    }

    // 更新视频评论数
    @Override
    public void addCommentNum(Long id) {
        videoMapper.addCommentNum(id);
    }

    //根据作品id查询作者
    @Override
    public Video getVideoById(Long id) {
        return videoMapper.getVideoById(id);
    }

    //根据用户名查询用户
    @Override
    public User getUserByUsername(String username) {
        return videoMapper.getUserByUsername(username);
    }

    //添加信息
    @Override
    public void addAtMessage(Message msg) {
        videoMapper.addAtMessage(msg);
    }
}
