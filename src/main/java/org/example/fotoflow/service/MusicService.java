package org.example.fotoflow.service;

import org.example.fotoflow.pojo.Music;

import java.util.List;

public interface MusicService {
    // 添加音乐
    void addMusic(Music music);

    // 随机推荐音乐
    List<Music> randomMusic();

    // 热门榜单
    List<Music> hotMusic();

    // 搜索音乐
    List<Music> searchMusic(String keyword);

    // 增加播放量
    void MusicCount(Long idId);
}
