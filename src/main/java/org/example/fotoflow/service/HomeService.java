package org.example.fotoflow.service;

import org.example.fotoflow.dto.WorkDto;
import org.example.fotoflow.pojo.*;

import java.util.List;
import java.util.Map;

public interface HomeService {
    // 查询发布的图片,一条一条的查询
    Work listWorksByPage();

    //多图片查询
    //Work listWorkImages();

    // 发布图片
    void addWork(WorkDto workDto,Long userId);

    // 根据id查询图片详情
    Work workDetail(Long id);

    // 根据用户图片id查询发布者用户
    User getUserById(Long userId);

    // 根据作品id查询所有评论
    List<Comment> listCommentsByWorkId(Long id);

    // 添加自己的评论
    void addComment(Comment comment);

    // 增加评论数+1
    void addCommentNum(Long id);

    // 根据作品id查询点赞信息,判断是否点赞了
    Like likeIsExist(Long id,Long userId);

    // 更新点赞数
    void updateLike(Long id, Long userId,int i);

    // 根据作品id查询收藏信息,判断是否收藏了
    Collect collectIsExist(Long id, Long userId);

    // 更新收藏数
    void updateCollect(Long id, Long userId, int i);

    // 更新作品点赞数
    void updateWorkLike(Long id, int i);

    // 更新作品收藏数
    void updateWorkCollect(Long id, int i);


    //实现作品多图查询
    List<Work> getWorkList(Integer pageNum);

    //根据作品id查找该作品
    Work getWorkById(Long id);
}
