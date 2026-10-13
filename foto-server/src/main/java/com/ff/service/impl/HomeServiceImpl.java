package com.ff.service.impl;


import com.ff.dto.WorkDto;
import com.ff.entity.*;
import com.ff.mapper.HomeMapper;
import com.ff.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HomeServiceImpl implements HomeService {
    @Autowired
    private HomeMapper homeMapper;
    @Override
    public Work listWorksByPage() {
        Work work = homeMapper.getRandomWork();

        return work;
    }

    // 查询多图片

//    @Override
//    public Work listWorkImages() {
//        // 1. 随机查1条作品（你原来的写法）
//        Work work = homeMapper.getRandomWork();
//
//        // 2. 直接查这个作品的所有图片（超级简单）
//        List<WorkImage> images = homeMapper.selectList(
//                new LambdaQueryWrapper<WorkImage>()
//                        .eq(WorkImage::getWorkId, work.getId())
//        );
//
//        // 转成图片地址列表
//        List<String> imgList = images.stream()
//                .map(WorkImage::getImgUrl)
//                .toList();
//
//        work.setImgList(imgList);
//
//        return work;
//    }

    // 发布图片
    @Override
    @Transactional
    public void addWork(WorkDto workDto, Long userId) {
        // 前端传的所有图片
        List<String> imgList = workDto.getImgList();

// 1. 保存作品（imgUrl 随便给一张，不影响业务）
        Work work = new Work();
        work.setUserId(userId);
        work.setTitle(workDto.getTitle());
        work.setContent(workDto.getContent());
        work.setCategory(workDto.getCategory());

// 👇 这里只是为了兼容数据库不报错，随便塞一张，以后不用它
        if (!imgList.isEmpty()) {
            work.setImgUrl(imgList.get(0));
        } else {
            work.setImgUrl(""); // 防止空
        }

// 插入作品，获取 id
        homeMapper.addWork(work);
        Long workId = work.getId();

// 2. 所有图片全部存进 work_image（没有封面！全部存）
        List<WorkImage> images = new ArrayList<>();
        for (int i = 0; i < imgList.size(); i++) {
            WorkImage img = new WorkImage();
            img.setWorkId(workId);
            img.setImgUrl(imgList.get(i));
            img.setSort(i + 1);
            images.add(img);
        }

        homeMapper.addWorkImage(images);
    }


    // 根据id查询图片详情
    @Override
    public Work workDetail(Long id) {
        return homeMapper.selectById(id);
    }

    // 根据用户图片id查询发布者用户名
    @Override
    public User getUserById(Long userId) {
        User user = homeMapper.selectUserById(userId);
//        System.out.println("查询用户ID：" + userId + "，查询结果：" + user);
        return user;
    }

    // 根据作品id查询所有评论
    @Override
    public List<Comment> listCommentsByWorkId(Long id) {
        List<Comment> comments = homeMapper.listCommentsByWorkId(id);
        return comments;
    }

    // 添加自己的评论
    @Override
    public void addComment(Comment comment) {
        homeMapper.addComment(comment);
    }

    // 增加评论数+1
    @Override
    public void addCommentNum(Long id) {
        homeMapper.updateCommentAdd(id);
    }

    // 根据作品id查询点赞信息,判断是否点赞了
    @Override
    public Like likeIsExist(Long id, Long userId) {
        return homeMapper.selectLikeByWorkIdAndUserId(id,userId);
    }

    // 更新点赞数
    @Override
    public void updateLike(Long id, Long userId,int i) {

        if(i==0){
            homeMapper.updateMyLikeAdd(id,userId);
        }
        else{
            homeMapper.updateMyLikeSub(id,userId);
        }

    }

    // 根据作品id查询收藏信息,判断是否收藏了
    @Override
    public Collect collectIsExist(Long id, Long userId) {
        return  homeMapper.selectCollectByWorkIdAndUserId(id,userId);
    }

    // 更新收藏数
    @Override
    public void updateCollect(Long id, Long userId, int i) {
        if(i==0){
            homeMapper.updateMyCollectAdd(id,userId);
        }
        else{
            homeMapper.updateMyCollectSub(id,userId);
        }
    }

    // 更新作品点赞数
    @Override
    public void updateWorkLike(Long id, int i) {
        if(i==0){
            homeMapper.updateWorkLikeAdd(id);
        }
        else{
            homeMapper.updateWorkLikeSub(id);
        }
    }

    // 更新作品收藏数
    @Override
    public void updateWorkCollect(Long id, int i) {
        if(i==0){
            homeMapper.updateWorkCollectAdd(id);
        }
        else{
            homeMapper.updateWorkCollectSub(id);
        }
    }

    //实现作品多图查询
    @Override
    public List<Work> getWorkList(Integer pageNum) {
        // 每次固定查 5 条
        int start = (pageNum - 1) * 5;
        int size = 5;

        // 1. 查作品
        List<Work> workList = homeMapper.getWorkList(start, size);

        // 2. 查每个作品的图片
        for (Work work : workList) {
            List<String> imgList = homeMapper.getImgListByWorkId(work.getId());
            work.setImgList(imgList);
        }


        return workList;
    }

    // 根据作品id查找该作品
    @Override
    public Work getWorkById(Long id) {
        return homeMapper.selectById(id);
    }

}
