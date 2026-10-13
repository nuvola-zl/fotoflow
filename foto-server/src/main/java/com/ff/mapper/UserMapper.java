package com.ff.mapper;

import com.ff.vo.RealNameVO;
import com.ff.entity.Collect;
import com.ff.entity.User;
import com.ff.entity.Work;
import com.ff.vo.UserInfoVO;
import org.apache.ibatis.annotations.*;


import java.util.List;

@Mapper
public interface UserMapper  {

    //根据用户id查询用户信息
    @Select("select * from user where id = #{userId}")
    UserInfoVO selectUserById(Long userId);

    //获取用户实名信息
    @Select("select is_real_name,real_name,id_card from user where id = #{userId}")
    RealNameVO selectRealName(Long userId);

    // 更新用户名
    @Update("update user set username = #{username} where id = #{userId}")
    void updateUsername(Long userId, String username);

    // 更新用户实名信息
    @Update("update user set phone = #{phone},is_real_name = #{isRealName}, real_name = #{realName},id_card = #{idCard} where id = #{userId}")
    void updateRealName(Long userId,String phone,Integer isRealName,String  realName, String idCard);

    // 获取用户密码
    @Select("select password from user where id = #{userId}")
    String getPassword(Long userId);

    // 更新用户密码
    @Update("update user set password = #{newPassword} where id = #{userId}")
    void updatePassword(Long userId, String newPassword);

    // 更新用户头像
    @Update("update user set avatar_url = #{url} where id = #{userId}")
    void updateAvatar(Long userId, String url);

    // 分页查询用户发布的图片
    @Select("""
    SELECT 
        id,
        user_id,
        title,
        content,
        img_url,
        category,
        state,
        reject_reason,
        create_time,
        update_time
    FROM work 
    WHERE user_id = #{userId} 
    ORDER BY id DESC 
    LIMIT #{start},#{pageSize}
""")
    List<Work> listWorkByPage(Long userId, int start, Integer pageSize);

    // 统计用户发布的图片数量
    @Select("select count(*) from work where user_id = #{userId}")
    Long countWork(Long userId);

    // 获取用户发布的图片详情
    @Select("select * from work where id = #{workId}")
    Work selectWorkById(Long workId);



    // 更新用户发布的图片信息（只改文字，不动图片）
    @Update("""
    update work 
    set title = #{title}, 
        content = #{content}, 
        category = #{category}
    where id = #{id}
""")
    void updateWork(Work work);

    // 删除用户发布的图片
    @Delete("delete from work where id = #{workId}")
    void deleteWork(Long workId);

    // 查询用户收藏的图片
    @Select("select * from user_work_collect where user_id = #{userId} ")
    List<Collect> listCollectByPage(Long userId);

    // 统计用户收藏的图片数量
    @Select("select count(*) from user_work_collect where user_id = #{userId}")
    Long countCollect(Long userId);

    // 批量根据id查询作品
    @Select("<script>"
            + "SELECT * FROM work WHERE id IN "
            + "<foreach collection='workIdList' open='(' separator=',' close=')' item='id'>"
            + "#{id}"
            + "</foreach>"
            + "</script>")
    List<Work> selectWorkByIdList(@Param("workIdList") List<Long> workIdList);


    // 根据用户id查询用户
    @Select("select * from user where id = #{userId}")
    User selectById(Long userId);

    // 根据用户名查询用户
    @Select("select * from user where username = #{username}")
    User selectByUsername(String username);
}
