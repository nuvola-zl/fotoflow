package org.example.fotoflow.controller;

import jakarta.annotation.Resource;
import org.example.fotoflow.pojo.Music;
import org.example.fotoflow.pojo.Result;
import org.example.fotoflow.service.MusicService;
import org.example.fotoflow.utils.AliOssUtil;
import org.example.fotoflow.utils.VideoDurationUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/music")
public class MusicController {

    @Resource
    private MusicService musicService;

    // 上传歌曲（最终推荐版）
    @PostMapping("/uploadMusic")
    public Result<String> uploadMusic(
            Music music,        // 直接用实体类接收 name、singer
            @RequestParam("file") MultipartFile file   // 文件单独传
    ) throws Exception {

        // 上传音乐
        String url = AliOssUtil.uploadMusic(file);
        music.setUrl(url);

        // 获取时长
        int duration = VideoDurationUtil.getDurationSeconds(file);
        music.setDuration(duration);

        // 保存数据库
        musicService.addMusic(music);

        return Result.success("上传成功");
    }

    //随机推荐音乐
    @GetMapping("/random")
    public Result<List<Music>> randomMusic() {
        return Result.success(musicService.randomMusic());
    }

    //热门榜单
    @GetMapping("/hot")
    public Result<List<Music>> hotMusic() {
        return Result.success(musicService.hotMusic());
    }

    //搜索音乐
    @GetMapping("/search")
    public Result<List<Music>> searchMusic(@RequestParam("keyword") String keyword) {
        return Result.success(musicService.searchMusic(keyword));
    }

    //增加播放量
    @PostMapping("/Count/{idId}")
    public Result<String> MusicCount(@PathVariable Long idId) {
        musicService.MusicCount(idId);
        return Result.success("请你点评");
    }
}
