package com.ff.controller;
import com.ff.constant.MessageConstant;
import com.ff.constant.RedisKeyConstant;
import com.ff.context.BaseContext;
import com.ff.dto.VideoPublishDto;
import com.ff.entity.*;
import com.ff.exception.CollectSetException;
import com.ff.exception.LikeSetException;
import com.ff.result.Result;
import com.ff.service.VideoService;
import com.ff.utils.VideoDurationUtil;
import com.ff.utils.WebSocketServer;
import com.ff.vo.BarrageVO;
import com.ff.vo.VideoVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.concurrent.*;

@RestController
@RequestMapping("/video")
public class VideoController {


    @Resource
    private VideoService videoService;

    /**
     * 发布视频
     */
    @PostMapping("/publish")
    public Result<String> publish(
            VideoPublishDto videoPublishDto
    )  {
        // 保存到数据库
        videoService.publishVideo(videoPublishDto);
        // 发送消息通知
        return Result.success(MessageConstant.PUBLISH_SUCCESS);
    }

    // 获取视频列表
    // 获取首页最新一条视频
    @GetMapping("/getLatestOne")
    public Result<VideoVO> getLatestOne() {
        VideoVO videoVO=videoService.getLatestOne();
        return Result.success(videoVO);
    }

    //发送弹幕
    @PostMapping("/sendBarrage/{videoId}")
    public Result<String> sendBarrage(@PathVariable Long videoId, @RequestBody Barrage barrage) {
        videoService.sendBarrage(barrage,videoId);
        return Result.success(MessageConstant.BARRAGE_SEND_SUCCESS);
    }

    // 获取视频弹幕
    @GetMapping("/getBarrages/{videoId}")
    public Result<List<BarrageVO>> getBarrages(@PathVariable Long videoId) {
        List<BarrageVO> barrages = videoService.getBarrages(videoId);
        return Result.success(barrages);
    }

    // 点赞
    @PostMapping("/like/{id}")
    public Result<Boolean> videoLike(@PathVariable Long id) {
          return Result.success(videoService.updateLikeAll(id));
    }


    // 收藏
    @PostMapping("collect/{id}")
    public Result<Boolean> videoCollect(@PathVariable Long id) {

        return Result.success(videoService.updateCollectAll(id));

    }

//    // 评论
//    //点击评论按钮，先回显所有评论，再显示评论框
//    //作品id
//    @GetMapping("comments/{id}")
//    public Result<List<Comment>> comment(@PathVariable Long id) {
//        List<Comment> comments = videoService.listCommentsByWorkId(id);
//        return Result.success(comments);
//    }
//    //在评论框中输入评论内容，点击发送生成评论
//    @PostMapping("comment/{id}")
//    public Result<String> addComment(@PathVariable Long id, @RequestBody Comment comment) {
//        // 从token中获取userId
//        Long userId = BaseContext.getCurrentId();
//
//        comment.setVideoId(id);
//        comment.setUserId(userId);
//
//        videoService.addComment(comment);
//
//        //增加评论数
//        videoService.addCommentNum(id);
//
//        // 根据作品ID查作者ID → 推送给作者
//        Video video = videoService.getVideoById(id);
//        WebSocketServer.sendToUser(video.getUserId(), "newComment");
//
//
//        String content = comment.getContent();
//
//        // 正则匹配：@xxx （匹配 @ + 非空格字符）
//        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("@(\\S+)");
//        java.util.regex.Matcher matcher = pattern.matcher(content);
//
//        // 遍历所有被 @ 的人
//        while (matcher.find()) {
//            // 获取 @ 的用户名
//            String username = matcher.group(1);
//
//            //根据用户名查询用户ID
//            User atUser = videoService.getUserByUsername(username);
//            if (atUser == null) continue; // 找不到就跳过
//            Long atUserId = atUser.getId();
//
//            // 给自己发消息提醒：你被 @ 了
//            Message msg = new Message();
//            msg.setFromUserId(userId);       // 谁@的
//            msg.setToUserId(atUserId);       // 被@的人
//            msg.setContent("有人在评论中@了你：" + content);
//            msg.setIsRead(0);
//            videoService.addAtMessage(msg);  // 保存@消息
//
//            // WebSocket 推送提醒
//            WebSocketServer.sendToUser(atUserId, "newAt");
//        }
//        // ====================== @用户功能 结束 ======================
//        return Result.success("评论成功");
//        //之后刷新品论界面，显示最新的评论
//    }

}