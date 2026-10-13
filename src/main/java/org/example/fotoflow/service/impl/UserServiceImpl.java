package org.example.fotoflow.service.impl;

import org.example.fotoflow.dto.RealNameDto;
import org.example.fotoflow.mapper.UserMapper;
import org.example.fotoflow.pojo.Collect;
import org.example.fotoflow.pojo.User;
import org.example.fotoflow.pojo.Work;
import org.example.fotoflow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    // 获取用户实名信息
    @Override
    public RealNameDto getRealName(Long userId) {
        return userMapper.selectRealName(userId);
    }

    //更改用户姓名
    @Override
    public void updateUsername(Long userId, String username) {
        userMapper.updateUsername(userId, username);
    }

    // 更新用户实名信息
    @Override
    public void updateRealName(Long userId, RealNameDto realNameDto) {

    String realName = realNameDto.getRealName();
    String idCard = realNameDto.getIdCard();
    String phone = realNameDto.getPhone();
    Integer isRealName = 1;

        // 更新用户实名信息
        userMapper.updateRealName( userId,phone,isRealName,realName,idCard);
    }

    // 获取用户密码
    @Override
    public String getPassword(Long userId) {
        return userMapper.getPassword(userId);
    }

    // 更新用户密码
    @Override
    public void updatePassword(Long userId, String newPassword) {
        userMapper.updatePassword(userId, newPassword);
    }

    // 更新用户头像
    @Override
    public void updateAvatar(Long userId, String url) {
        userMapper.updateAvatar(userId, url);
    }

    @Override
    public Map<String, Object> getWork(Long userId, Integer pageNum, Integer pageSize) {
        int start = (pageNum - 1) * pageSize;

        // 1. 查询当前页图片列表
        List<Work> workList = userMapper.listWorkByPage(userId, start, pageSize);

        Long total = userMapper.countWork(userId);

        Map<String, Object> map = new HashMap<>();
        map.put("workList", workList);
        map.put("total", total);

        return map;
    }

    // 获取用户发布的图片详情
    @Override
    public Work getWorkById(Long workId) {
        return userMapper.selectWorkById(workId);
    }

    //在我发布的图片详情里面显示创建人的姓名
    @Override
    public User getUserById(Long userId) {
        return userMapper.selectUser(userId);
    }

    // 更新用户发布的图片信息
    @Override
    public void updateWork( Work work) {
        userMapper.updateWork(work);
    }

    // 删除用户发布的图片
    @Override
    public void deleteWork(Long workId) {
        userMapper.deleteWork(workId);

    }



    @Override
    public Map<String, Object> getCollects(Long userId) {
        // 1. 查询当前用户所有收藏记录
        List<Collect> collectList = userMapper.listCollectByPage(userId);

        // 2. 提取所有workId
        List<Long> workIdList = new ArrayList<>();
        for (Collect c : collectList) {
            workIdList.add(c.getWorkId());
        }

        List<Work> workList = new ArrayList<>();
        // 判空：没收藏直接返回空
        if (!workIdList.isEmpty()) {
            // 3. 【关键】批量根据id查作品，只查1次库
            workList = userMapper.selectWorkByIdList(workIdList);
        }

        // 4. 总数
        Long total = userMapper.countCollect(userId);

        // 5. 返回
        Map<String, Object> map = new HashMap<>();
        map.put("workList", workList);
        map.put("total", total);
        return map;
    }
}
