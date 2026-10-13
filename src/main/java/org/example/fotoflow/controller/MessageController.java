package org.example.fotoflow.controller;

import org.example.fotoflow.dto.ContactUserVO;
import org.example.fotoflow.pojo.Friend;
import org.example.fotoflow.pojo.Message;
import org.example.fotoflow.pojo.Result;
import org.example.fotoflow.pojo.User;
import org.example.fotoflow.service.MessageService;
import org.example.fotoflow.utils.JwtUtils;
import org.example.fotoflow.utils.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Message")
public class MessageController {
    @Autowired
    private MessageService messageService;
    //点击作品上的头像进入发布作品的人个人主页
    @GetMapping("/toUser/{userId}")
    public Result toUser(@PathVariable Long userId){
        return Result.success(messageService.toUser(userId));
    }


    //关注用户，使他成为我的好友
    @PostMapping("/follow/{userId}")
    public Result follow(@PathVariable Long userId,@RequestHeader("token") String token){
        //先获取当前登录用户id
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long myId = ((Number) claims.get("userId")).longValue();
        messageService.follow(myId, userId);

        //增加他的粉丝数目
        messageService.addFan(userId);
        return Result.success("关注成功");
    }


    //点击关注按钮，取消关注
    @PostMapping("/unfollow/{userId}")
    public Result unfollow(@PathVariable Long userId,@RequestHeader("token") String token){
        //先获取当前登录用户id
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long myId = ((Number) claims.get("userId")).longValue();
        messageService.unfollow(myId, userId);

        //减少他的粉丝数目
        messageService.subFan(userId);
        return Result.success("已取消关注");
    }

    //点击@   弹出用户列表
    @GetMapping("/atUser")
    public Result atUser(@RequestHeader("token") String token){
        //先获取当前登录用户id
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long myId = ((Number) claims.get("userId")).longValue();
        //查询所有我关注过的用户
        return Result.success(messageService.atUser(myId));
    }


    //私信用户1.获取信息
    @GetMapping("/getMessage/{userId}")
    public Result getMessage(@PathVariable Long userId,@RequestHeader("token") String token) {

        //先获取当前登录用户id
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long myId = ((Number) claims.get("userId")).longValue();
        return Result.success(messageService.getMessageList(myId, userId));
    }

    //私信用户2.发送信息
    @PostMapping("/sendMessage/{userId}")
    public Result sendMessage(@PathVariable Long userId,@RequestBody Message message,@RequestHeader("token") String token){
        //先获取当前登录的id
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long myId = ((Number) claims.get("userId")).longValue();
        messageService.sendMessage(myId, userId, message);

        // ==================== 私信实时推送 ====================
        WebSocketServer.sendToUser(userId, "newMessage");
        // ====================================================

        return Result.success();
    }

    //查询所有我联系过的用户
    @GetMapping("/getAllContact")
    public Result getAllContact(@RequestHeader("token") String token){
        //先获取当前登录用户id
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long myId = ((Number) claims.get("userId")).longValue();
        List<ContactUserVO> contactList = messageService.getAllContact(myId);
        return Result.success(contactList);
    }

    //点开消息列表之后，将所有未读消息标记为已读
    @PostMapping("/markAllRead/{userId}")
    public Result markAllRead(@PathVariable Long userId,@RequestHeader("token") String token){
        //先获取当前登录用户id
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long myId = ((Number) claims.get("userId")).longValue();
        //标记为已读
        messageService.markAllRead(myId, userId);
        return Result.success();
    }

}
