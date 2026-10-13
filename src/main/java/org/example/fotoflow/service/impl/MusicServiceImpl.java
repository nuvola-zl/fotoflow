package org.example.fotoflow.service.impl;

import jakarta.annotation.Resource;
import org.example.fotoflow.mapper.MusicMapper;
import org.example.fotoflow.pojo.Music;
import org.example.fotoflow.service.MusicService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MusicServiceImpl implements MusicService {
    @Resource
    private MusicMapper musicMapper;

    // 添加音乐
    @Override
    public void addMusic(Music music) {
        // 保存到数据库
        musicMapper.insertMusic(music);
    }

    // 随机推荐音乐
    @Override
    public List<Music> randomMusic() {
        return musicMapper.randomMusic();
    }

    // 热门榜单
    @Override
    public List<Music> hotMusic() {
        return musicMapper.hotMusic();
    }

    // 搜索音乐
    @Override
    public List<Music> searchMusic(String keyword) {
        return musicMapper.searchMusic(keyword);
    }

    // 增加播放量
    @Override
    public void MusicCount(Long idId) {
        musicMapper.MusicCount(idId);
    }
}
