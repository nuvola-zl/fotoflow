package com.ff.mapper;

import com.ff.entity.*;
import com.ff.vo.BarrageVO;
import org.apache.ibatis.annotations.*;


import java.util.List;

@Mapper
public interface VideoMapper {

    @Insert("""
    insert into video 
    (user_id, title, content, video_url, duration, state, create_time, update_time) 
    values 
    (#{userId}, #{title}, #{content}, #{videoUrl}, #{duration}, 0, now(), now())
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
    List<BarrageVO> getBarrages(Long videoId);


    /**
     *p判断点赞/收藏是否存在
     * @param table
     * @param videoId
     * @param userId
     * @return
     */
    int checkExist(
            @Param("table") String table,
            @Param("videoId") Long videoId,
            @Param("userId") Long userId
    );

    /**
     * 通用更新计数字段（like_count / collect_count）
     * @param videoId 视频ID
     * @param num 更新数量
     */
    void updateCount(
            @Param("column") String column,
            @Param("videoId") Long videoId,
            @Param("num") int num
    );

    /**
     * 通用点赞/收藏 插入/删除 二合一
     * @param table 表名 user_video_like / user_video_collect
     * @param videoId 视频ID
     * @param userId 用户ID
     * @param type 1=插入 -1=删除
     */
    void toggleRelation(
            @Param("table") String table,
            @Param("videoId") Long videoId,
            @Param("userId") Long userId,
            @Param("type") int type
    );


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

    @Update("update video set duration = #{duration} where id = #{id}")
    void updateById(Video video);
}