package org.example.fotoflow.controller;

import jakarta.annotation.Resource;
import org.example.fotoflow.pojo.*;
import org.example.fotoflow.service.VideoService;
import org.example.fotoflow.utils.AliOssUtil;
import org.example.fotoflow.utils.JwtUtils;
import org.example.fotoflow.utils.VideoDurationUtil;
import org.example.fotoflow.utils.WebSocketServer;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Resource
    private VideoService videoService;

    /**
     * 发布视频
     */
    @PostMapping("/publish")
    public Result publish(
            String title,
            String content,
            MultipartFile videoFile,    // 必传：视频文件
            MultipartFile coverFile ,    // 可选：封面图
            @RequestHeader("token") String token
    ) throws Exception {
        // 从token中获取userId
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long userId = ((Number) claims.get("userId")).longValue();
        // 1. 上传视频到阿里云
        String videoUrl = AliOssUtil.uploadVideo(videoFile);
        // 2. 自动获取视频时长（秒）
        int duration = VideoDurationUtil.getDurationSeconds(videoFile);
        // 3. 上传封面（如果有）
        String coverUrl = null;
        if (coverFile != null && !coverFile.isEmpty()) {
            coverUrl = AliOssUtil.upload(coverFile);
        }
        // 4. 封装数据
        Video video = new Video();
        video.setTitle(title);
        video.setContent(content);
        video.setVideoUrl(videoUrl);
        video.setCoverUrl(coverUrl);
        video.setDuration(duration);
        video.setUserId(userId);
        // 5. 保存到数据库
        videoService.publishVideo(video);
        return Result.success("视频发布成功");
    }

    // 获取视频列表
    // 获取首页最新一条视频
    @GetMapping("/getLatestOne")
    public Result getLatestOne(@RequestHeader("token") String token) {
        // 1. 获取当前登录用户ID
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long userId = ((Number) claims.get("userId")).longValue();

        // 2. 获取视频
        Video video = videoService.getLatestOne();

        // 3. 查询当前用户 是否点赞 + 是否收藏
        boolean isLike = videoService.likeIsExist(video.getId(), userId) != null;
        boolean isCollect = videoService.collectIsExist(video.getId(), userId) != null;

        // 4. 塞进 video 对象
        video.setIsLike(isLike);
        video.setIsCollect(isCollect);

        return Result.success(video);
    }

    //发送弹幕
    @PostMapping("/sendBarrage/{videoId}")
    public Result sendBarrage(@PathVariable Long videoId, @RequestBody Barrage barrage, @RequestHeader("token") String token) {
        // 从token中获取userId
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long userId = ((Number) claims.get("userId")).longValue();
        barrage.setUserId(userId);
        barrage.setVideoId(videoId);
        videoService.sendBarrage(barrage);
        return Result.success("弹幕发送成功");
    }

    // 获取视频弹幕
    @GetMapping("/getBarrages/{videoId}")
    public Result getBarrages(@PathVariable Long videoId) {
        List<Barrage> barrages = videoService.getBarrages(videoId);
        return Result.success(barrages);
    }

    // 点赞
    @PostMapping("/like/{id}")
    public Result videoLike(@PathVariable Long id,@RequestHeader("token") String token) {

        // 从token中获取userId
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long userId = ((Number) claims.get("userId")).longValue(); //  安全转换

        //先判断是否已经点赞了

        Like like = videoService.likeIsExist(id,userId);
        if(like==null) {
            //没点赞过，点赞数增加1
            // 点赞成功后，刷新点赞按钮，显示点赞数增加1
            //点赞按钮的点赞图标改变为已点赞图标
            videoService.updateWorkLike(id,1);
            videoService.updateLike(id, userId,1);
            return Result.success(true);
        }
        else {
            //点赞过，点赞数减少1
            // 点赞成功后，刷新点赞按钮，显示点赞数减少1
            //点赞按钮的点赞图标改变为未点赞图标
            videoService.updateWorkLike(id,-1);
            videoService.updateLike(id, userId,0);
            return Result.success(false);
        }

    }


    // 收藏
    @PostMapping("collect/{id}")
    public Result videoCollect(@PathVariable Long id,@RequestHeader("token") String token) {
        // 从token中获取userId
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long userId = ((Number) claims.get("userId")).longValue(); // 安全转换
        //先判断是否已经收藏了
        Collect collect = videoService.collectIsExist(id,userId);
        if(collect==null) {
            //没收藏过，收藏数增加1

            videoService.updateVideoCollect(id,1);
            videoService.updateMyCollect(id, userId,1);
            return Result.success(true);
        }
        else {
            //收藏过，收藏数减少1
            //TODO更新work表的collectNum
            videoService.updateVideoCollect(id,-1);
            videoService.updateMyCollect(id, userId,0);
            return Result.success(false);
        }

    }

    // 评论
    //点击评论按钮，先回显所有评论，再显示评论框
    //作品id
    @GetMapping("comments/{id}")
    public Result comment(@PathVariable Long id) {
        List<Comment> comments = videoService.listCommentsByWorkId(id);
        return Result.success(comments);
    }
    //在评论框中输入评论内容，点击发送生成评论
    @PostMapping("comment/{id}")
    public Result addComment(@RequestHeader("token") String token,@PathVariable Long id, @RequestBody Comment comment) {
        // 从token中获取userId
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long userId = ((Number) claims.get("userId")).longValue(); //  安全转换
        comment.setVideoId(id);
        comment.setUserId(userId);
        videoService.addComment(comment);



        //增加评论数
        videoService.addCommentNum(id);

        // ==================== 只加这一行 ====================
        // 根据作品ID查作者ID → 推送给作者
        Video video = videoService.getVideoById(id);
        WebSocketServer.sendToUser(video.getUserId(), "newComment");
        // ===================================================




        // ====================== @用户功能 开始 ======================
        String content = comment.getContent();

        // 正则匹配：@xxx （匹配 @ + 非空格字符）
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("@(\\S+)");
        java.util.regex.Matcher matcher = pattern.matcher(content);

        // 遍历所有被 @ 的人
        while (matcher.find()) {
            // 获取 @ 的用户名
            String username = matcher.group(1);

            //根据用户名查询用户ID
            User atUser = videoService.getUserByUsername(username);
            if (atUser == null) continue; // 找不到就跳过
            Long atUserId = atUser.getId();

            // 给自己发消息提醒：你被 @ 了
            Message msg = new Message();
            msg.setFromUserId(userId);       // 谁@的
            msg.setToUserId(atUserId);       // 被@的人
            msg.setContent("有人在评论中@了你：" + content);
            msg.setIsRead(0);
            videoService.addAtMessage(msg);  // 保存@消息

            // WebSocket 推送提醒
            WebSocketServer.sendToUser(atUserId, "newAt");
        }
        // ====================== @用户功能 结束 ======================
        return Result.success("评论成功");
        //之后刷新品论界面，显示最新的评论
    }


}