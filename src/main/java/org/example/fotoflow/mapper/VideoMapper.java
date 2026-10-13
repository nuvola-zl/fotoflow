package org.example.fotoflow.mapper;

import org.apache.ibatis.annotations.*;
import org.example.fotoflow.pojo.*;

import java.util.List;

@Mapper
public interface VideoMapper {

    @Insert("""
        insert into video 
        (user_id, title, content, video_url, cover_url, duration, state, create_time, update_time) 
        values 
        (#{userId}, #{title}, #{content}, #{videoUrl}, #{coverUrl}, #{duration}, 0, now(), now())
    """)
    void publishVideo(Video video);

    // 查询1条视频（审核通过的）//先测试
    @Select("select * from video where state = 0 order by RAND() limit 1")
    Video getLatestOne();

    // 发送弹幕
    @Insert("""
        insert into barrage 
        (video_id, user_id, content, color, video_time, create_time) 
        values 
        (#{videoId}, #{userId}, #{content}, #{color}, #{videoTime}, now())
    """)
    void sendBarrage(Barrage barrage);

    // 获取视频弹幕
    @Select("select * from barrage where video_id = #{videoId} order by video_time asc")
       List<Barrage> getBarrages(Long videoId);

    //判断用户是否点赞过该视频
    @Select("select * from user_video_like where video_id = #{id} and user_id = #{userId}")
    Like likeIsExist(Long id, Long userId);

    // 更新视频点赞数
    @Update("update video set like_count = like_count + #{i} where id = #{id}")
    void updateVideoLike(Long id, int i);

    // 更新用户点赞状态+1
    @Update("insert into user_video_like (video_id, user_id) values (#{id}, #{userId})")
    void updateMyLikeAdd(Long id, Long userId);

    // 更新用户点赞状态-1
    @Delete("delete from user_video_like where video_id = #{id} and user_id = #{userId}")
    void updateMyLikeSub(Long id, Long userId);

    //更新作品的收藏数目
    @Update("update video set collect_count = collect_count + #{i} where id = #{id}")
    void updateVideoCollect(Long id, int i);

    // 更新用户收藏状态+1
    @Update("insert into user_video_collect (video_id, user_id) values (#{id}, #{userId})")
    void updateMyCollectAdd(Long id, Long userId);

    // 更新用户收藏状态-1
    @Delete("delete from user_video_collect where video_id = #{id} and user_id = #{userId}")
    void updateMyCollectSub(Long id, Long userId);

    //判断用户是否收藏过该视频
    @Select("select * from user_video_collect where video_id = #{id} and user_id = #{userId}")
    Collect collectIsExist(Long id, Long userId);

    //获取评论列表
    @Select("select * from video_comment where video_id = #{id} order by create_time desc")
    List<Comment> listCommentsByWorkId(Long id);

    // 添加评论
    @Insert("""
        insert into video_comment 
        (video_id, user_id, content, create_time) 
        values 
        (#{videoId}, #{userId}, #{content}, now())
    """)
    void addComment(Comment comment);

    // 更新视频评论数
    @Update("update video set comment_count = comment_count + 1 where id = #{id}")
    void addCommentNum(Long id);

    //根据作品id查询作者
    @Select("select * from video where id = #{id}")
    Video getVideoById(Long id);

    //根据用户名查询用户ID
    @Select("select * from user where username = #{username}")
    User getUserByUsername(String username);

    //添加信息
    @Insert("""
        insert into chat_message 
        (from_user_id, to_user_id, content, create_time) 
        values 
        (#{fromUserId}, #{toUserId}, #{content}, now())
    """)
    void addAtMessage(Message msg);
}