package org.example.fotoflow.service;

import org.apache.ibatis.annotations.Param;
import org.example.fotoflow.dto.RealNameDto;
import org.example.fotoflow.pojo.User;
import org.example.fotoflow.pojo.Work;

import java.util.Map;

public interface UserService {
    // 获取用户实名信息
    RealNameDto getRealName(Long userId);

    // 更新用户名
    void updateUsername(Long userId, String username);

    // 更新用户实名信息
    void updateRealName(Long userId, RealNameDto realNameDto);

    // 获取用户密码
    String getPassword(Long userId);

    // 更新用户密码
    void updatePassword(Long userId, String newPassword);

    //更新用户头像
    void updateAvatar(Long userId, String url);

    // 获取用户发布的图片
    Map<String, Object> getWork(Long userId, Integer pageNum, Integer pageSize);

    // 获取用户发布的图片详情
    Work getWorkById(Long workId);

    //在我发布的图片详情里面显示创建人的姓名
    User getUserById(Long userId);

    // 更新用户发布的图片信息
    void updateWork( Work work);

    // 删除用户发布的图片
    void deleteWork(Long workId);


    // 获取用户收藏的图片
    Map<String, Object> getCollects(Long userId);


}
