package com.ff.service;



import com.ff.dto.ContactUserVO;
import com.ff.entity.User;
import com.ff.entity.Message;


import java.util.List;

public interface MessageService {
    //点击作品上的头像进入发布作品的人个人主页
    User toUser(Long userId);

    //私信用户1.获取信息
    List<Message> getMessageList(Long myId, Long userId);

    //私信用户2.发送信息
    void sendMessage(Long myId, Long userId, Message message);

    //查询所有联系过我的用户
    List<ContactUserVO> getAllContact(Long myId);

    //标记未读消息为已读
    void markAllRead(Long myId, Long userId);

    //关注用户，使他成为我的好友
    void follow(Long myId, Long userId);

    //取消关注
    void unfollow(Long myId, Long userId);

    //查询所有我关注过的用户
    List<User> atUser(Long myId);

    //增加他的粉丝数目
    void addFan(Long userId);

    //减少他的粉丝数目
    void subFan(Long userId);
}
