package org.example.fotoflow.service.impl;

import jakarta.annotation.Resource;
import org.example.fotoflow.dto.ContactUserVO;
import org.example.fotoflow.mapper.MessageMapper;
import org.example.fotoflow.pojo.Friend;
import org.example.fotoflow.pojo.Message;
import org.example.fotoflow.pojo.User;
import org.example.fotoflow.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    @Resource
    private MessageMapper messageMapper;
    //点击作品上的头像进入发布作品的人个人主页
    @Override
    public User toUser(Long userId) {
        return messageMapper.selectUserById(userId);
    }

    //私信用户1.获取信息
    @Override
    public List<Message> getMessageList(Long myId, Long userId) {

        return messageMapper.getMessageList(myId,userId);
    }



    //私信用户2.发送消息
    @Override
    public void sendMessage(Long myId, Long userId, Message message) {
        message.setFromUserId(myId);
        message.setToUserId(userId);
        messageMapper.insert(message);
    }

    //查询所有联系过我的用户
    @Override
    public List<ContactUserVO> getAllContact(Long myId) {
        return messageMapper.getAllContact(myId);
    }

    //标记未读消息为已读
    @Override
    public void markAllRead(Long myId, Long userId) {
        messageMapper.markAllRead(myId, userId);
    }

    //关注用户，使他成为我的好友
    @Override
    public void follow(Long myId, Long userId) {
        messageMapper.follow(myId, userId);
    }

    //取消关注
    @Override
    public void unfollow(Long myId, Long userId) {
        messageMapper.unfollow(myId, userId);
    }

    //获取关注的用户列表
    @Override
    public List<User> atUser(Long myId) {
        return messageMapper.atUser(myId);
    }

    //增加他的粉丝数目
    @Override
    public void addFan(Long userId) {
        messageMapper.addFan(userId);
    }

    //减少他的粉丝数目
    @Override
    public void subFan(Long userId) {
        messageMapper.subFan(userId);
    }
}
