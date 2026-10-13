package com.ff.controller;
import com.ff.constant.MessageConstant;
import com.ff.dto.VideoPublishDto;
import com.ff.entity.*;
import com.ff.result.Result;
import com.ff.service.VideoService;
import com.ff.vo.BarrageVO;
import com.ff.vo.VideoVO;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/video")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    /**
     * 发布视频
     */
    @PostMapping("/publish")
    public Result<String> publish(
            @RequestBody VideoPublishDto videoPublishDto
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
    @PostMapping("/collect/{id}")
    public Result<Boolean> videoCollect(@PathVariable Long id) {

        return Result.success(videoService.updateCollectAll(id));

    }

}