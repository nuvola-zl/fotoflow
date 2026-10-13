package org.example.fotoflow.mapper;

import org.apache.ibatis.annotations.*;
import org.example.fotoflow.pojo.*;

import java.util.List;

@Mapper
public interface HomeMapper {
    // 随机查1条，新数据更容易出现，状态为通过的作品
    @Select("SELECT * FROM work WHERE state = 1 ORDER BY RAND() LIMIT 1")
    Work getRandomWork();

    //查询数据总条数
    @Select("select count(*) from work")
    Long countGoods();

    //上传作品，获取id
    @Insert("insert into work(user_id, title, content, img_url, category) " +
            "values(#{userId}, #{title}, #{content}, #{imgUrl}, #{category})")
    @Options(useGeneratedKeys = true, keyProperty = "id") // 自动返回主键id
    void addWork(Work work);

    // 添加作品图片...多张图片
    @Insert({
            "<script>",
            "insert into work_image(work_id, img_url, sort) values ",
            "<foreach collection='list' item='item' separator=','>",
            "(#{item.workId}, #{item.imgUrl}, #{item.sort})",
            "</foreach>",
            "</script>"
    })

    void addWorkImage(List<WorkImage> images);

    // 根据id查询图片详情
    @Select("select * from work where id = #{id}")
    Work selectById(Long id);

    // 根据用户id查询用户信息
    @Select("select * from user where id = #{userId}")
    User selectUserById(Long userId);

        @Select("""
        SELECT 
            c.*,
            u.username,
            u.avatar_url AS avatar
        FROM comment c
        INNER JOIN user u ON c.user_id = u.id
        WHERE c.work_id = #{workId}
        ORDER BY c.create_time DESC
    """)
        List<Comment> listCommentsByWorkId(Long workId);


    // 添加自己的评论
    @Insert("insert into comment (work_id, user_id, content) " +
            "values (#{workId}, #{userId}, #{content})")
    void addComment(Comment comment);

    // 增加评论数+1
    @Update("update work set comment_count = comment_count + 1 where id = #{id}")
    void updateCommentAdd(Long id);

    // 根据作品id查询点赞信息,判断是否点赞了
    @Select("select * from user_work_like where work_id = #{id} and user_id = #{userId}")
    Like selectLikeByWorkIdAndUserId(Long id, Long userId);

    //给我的点赞表添加点赞的作品
    @Insert("insert into user_work_like (user_id, work_id) values (#{userId}, #{id})")
    void updateMyLikeAdd(Long id, Long userId);

    //给我的点赞表减少点赞的作品
    @Delete("delete from user_work_like where user_id = #{userId} and work_id = #{id}")
    void updateMyLikeSub(Long id, Long userId);

    //判断是否收藏了
    @Select("select * from user_work_collect where work_id = #{id} and user_id = #{userId}")
    Collect selectCollectByWorkIdAndUserId(Long id, Long userId);

    // 增加我的收藏列表
    @Insert("insert into user_work_collect (user_id, work_id) values (#{userId}, #{id})")
    void updateMyCollectAdd(Long id, Long userId);

    // 减少我的收藏列表
    @Delete("delete from user_work_collect where user_id = #{userId} and work_id = #{id}")
    void updateMyCollectSub(Long id, Long userId);

    // 增加作品点赞数+1
   @Update("update work set like_count = like_count + 1 where id = #{id}")
    void updateWorkLikeAdd(Long id);

    // 减少作品点赞数-1
    @Update("update work set like_count = like_count - 1 where id = #{id}")
    void updateWorkLikeSub(Long id);

    // 增加作品收藏数+1
    @Update("update work set collect_count = collect_count + 1 where id = #{id}")
    void updateWorkCollectAdd(Long id);

    // 减少作品收藏数-1
    @Update("update work set collect_count = collect_count - 1 where id = #{id}")
    void updateWorkCollectSub(Long id);


    // 查询多作品（正确版）
    @Select("SELECT * FROM work WHERE state = 0 ORDER BY create_time DESC LIMIT #{start}, #{size}")
    List<Work> getWorkList(@Param("start") int start, @Param("size") int size);

    // 查询图片（不变，非常正确）
    @Select("SELECT img_url FROM work_image WHERE work_id = #{workId} ORDER BY sort")
    List<String> getImgListByWorkId(Long workId);
}
