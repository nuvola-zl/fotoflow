package com.ff.service;



import com.ff.dto.RealNameDto;
import com.ff.dto.UserUpdateDto;
import com.ff.vo.RealNameVO;
import com.ff.entity.User;
import com.ff.entity.Work;
import com.ff.vo.UserInfoVO;

import java.util.Map;

public interface UserService {

    // 根据用户id查询用户
    UserInfoVO getUserById(Long userId);

//    //
//
//    // 更新用户信息
//    String updateUserInfo(UserUpdateDto userUpdateDto);
//
//
//    // 获取用户实名信息
//    RealNameVO getRealName(Long userId);
//
//    // 更新用户名
//    void updateUsername(Long userId, String username);
//
//    // 更新用户实名信息
//    void updateRealName(Long userId, RealNameDto realNameDto);
//
//    // 获取用户密码
//    String getPassword(Long userId);
//
//    // 更新用户密码
//    void updatePassword(Long userId, String newPassword);
//
//    //更新用户头像
//    void updateAvatar(Long userId, String url);
//
//    // 获取用户发布的图片
//    Map<String, Object> getWork(Long userId, Integer pageNum, Integer pageSize);
//
//    // 获取用户发布的图片详情
//    Work getWorkById(Long workId);
//
//
//
//    // 更新用户发布的图片信息
//    void updateWork( Work work);
//
//    // 删除用户发布的图片
//    void deleteWork(Long workId);
//
//
//    // 获取用户收藏的图片
//    Map<String, Object> getCollects(Long userId);
//
//
//    // 根据用户名查询用户
     User getUserByUsername(String username);
}
