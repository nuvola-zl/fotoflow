package com.ff.mapper;

import com.ff.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface LoginMapper {

    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    @Select("select * from user where username = #{username}")
    User findByUsername(String username);

    /**
     * 注册用户
     * @param user
     */
    @Insert("insert into user(username, password, phone, is_real_name, role, status, ban_reason, fans_count, avatar_url) values(#{username}, #{password}, #{phone}, #{isRealName}, #{role}, #{status}, #{banReason}, #{fansCount}, #{avatarUrl})")
    void registerUser(User user);
}
