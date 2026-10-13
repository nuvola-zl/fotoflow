package com.ff.mapper;

import com.ff.annotation.AutoFill;
import com.ff.entity.*;
import com.ff.vo.BarrageVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 视频数据库操作 Mapper
 * 全部使用静态SQL，彻底消除SQL注入风险
 * 点赞、收藏、评论、弹幕、视频操作全部独立实现
 */
@Mapper
public interface VideoMapper {

    /**
     * 发布视频
     * AutoFill：自动填充 createTime、updateTime 等公共字段
     */
    @AutoFill("INSERT")
    @Insert("""
    insert into video 
    (user_id, title, content, video_url, duration, state, status, create_time, update_time) 
    values 
    (#{userId}, #{title}, #{content}, #{videoUrl}, #{duration}, 0, #{status}, #{createTime}, #{updateTime})
    """)
    void publishVideo(Video video);

    /**
     * 获取视频最大ID（用于高性能随机）
     */
    @Select("select max(id) from video where state = 0")
    Long getMaxVideoId();

    /**
     * 根据随机ID 快速查询一条视频（性能爆炸）
     */
    @Select("""
    select * from video 
    where state = 0 and id >= #{randomId} 
    limit 1
""")
    Video getRandomVideo(@Param("randomId") long randomId);

    /**
     * 兜底：防止随机到已删除数据
     */
    @Select("""
    select * from video 
    where state = 0 and id < #{randomId} 
    order by id desc limit 1
""")
    Video getRandomVideoFallback(@Param("randomId") long randomId);

    // ==================== 弹幕相关 ====================
    /**
     * 发送弹幕
     */
    @Insert("""
        insert into barrage 
        (video_id, user_id, content, color, video_time, create_time) 
        values 
        (#{videoId}, #{userId}, #{content}, #{color}, #{videoTime}, now())
    """)
    void sendBarrage(Barrage barrage);

    /**
     * 查询视频的所有弹幕（按时间正序）
     */
    @Select("select * from barrage where video_id = #{videoId} order by video_time asc")
    List<BarrageVO> getBarrages(Long videoId);

    // ==================== 点赞相关（静态SQL，安全） ====================
    /**
     * 查询用户是否已点赞
     */
    @Select("select count(*) from user_video_like where video_id = #{videoId} and user_id = #{userId}")
    int checkLikeExist(@Param("videoId") Long videoId, @Param("userId") Long userId);

    /**
     * 更新视频点赞数 +1 或 -1
     */
    @Update("update video set like_count = like_count + #{num} where id = #{videoId}")
    int updateLikeCount(@Param("videoId") Long videoId, @Param("num") int num);

    /**
     * 插入点赞记录
     */
    @Insert("insert into user_video_like (video_id, user_id) values (#{videoId}, #{userId})")
    int insertLike(@Param("videoId") Long videoId, @Param("userId") Long userId);

    /**
     * 删除点赞记录（取消点赞）
     */
    @Delete("delete from user_video_like where video_id = #{videoId} and user_id = #{userId}")
    int deleteLike(@Param("videoId") Long videoId, @Param("userId") Long userId);

    // ==================== 收藏相关（静态SQL，安全） ====================
    /**
     * 查询用户是否已收藏
     */
    @Select("select count(*) from user_video_collect where video_id = #{videoId} and user_id = #{userId}")
    int checkCollectExist(@Param("videoId") Long videoId, @Param("userId") Long userId);

    /**
     * 更新收藏数 +1 或 -1
     */
    @Update("update video set collect_count = collect_count + #{num} where id = #{videoId}")
    int updateCollectCount(@Param("videoId") Long videoId, @Param("num") int num);

    /**
     * 插入收藏记录
     */
    @Insert("insert into user_video_collect (video_id, user_id) values (#{videoId}, #{userId})")
    int insertCollect(@Param("videoId") Long videoId, @Param("userId") Long userId);

    /**
     * 删除收藏记录（取消收藏）
     */
    @Delete("delete from user_video_collect where video_id = #{videoId} and user_id = #{userId}")
    int deleteCollect(@Param("videoId") Long videoId, @Param("userId") Long userId);

    /**
     * 更新视频信息（如时长、状态）
     */
    @Update("update video set duration = #{duration}, status = #{status} where id = #{id}")
    void updateById(Video video);

    /**
     * 更新视频状态
     */
    @Update("update video set status = #{status} where id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 查询视频时长解析失败的任务（用于定时任务重试）
     */
    @Select("select * from video where status = 'DURATION_FAILED'")
    List<Video> findFailedDurationTasks();
}