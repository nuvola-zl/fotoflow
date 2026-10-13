package com.ff.mapper;

import com.ff.entity.User;
import com.ff.vo.UserInfoVO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper  {

    //根据用户id查询用户信息
    @Select("select * from user where id = #{userId}")
    UserInfoVO selectUserById(Long userId);


    // 根据用户id查询用户
    @Select("select * from user where id = #{userId}")
    User selectById(Long userId);

    // 根据用户名查询用户
    @Select("select * from user where username = #{username}")
    User selectByUsername(String username);

    /**
     *更新用户信息
     * @param user
     */
    @Update("update user set username = #{username}, password = #{password}, avatar_url = #{avatarUrl} where id = #{id}")
    int updateInfo(User user);
}
