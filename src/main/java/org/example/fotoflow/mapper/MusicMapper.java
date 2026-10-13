package org.example.fotoflow.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.fotoflow.pojo.Music;

import java.util.List;

@Mapper
public interface MusicMapper {
    // 添加音乐
    @Insert("insert into music (name, singer, url, duration) values (#{name}, #{singer}, #{url}, #{duration})")
    void insertMusic(Music music);

    // 随机推荐音乐
    @Select("select * from music order by rand() limit 5")
    List<Music> randomMusic();

    // 热门榜单
    @Select("select * from music order by play_count desc limit 20")
    List<Music> hotMusic();

    // 搜索音乐
    @Select("select * from music where name like concat('%', #{keyword}, '%') or singer like concat('%', #{keyword}, '%')")
    List<Music> searchMusic(String keyword);

    // 增加播放量
    @Update("update music set play_count = play_count + 1 where id = #{idId}")
    void MusicCount(Long idId);
}
