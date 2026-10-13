package com.ff.mapper;

import com.ff.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface LoginMapper {

    // 根据用户名查询用户
    @Select("select * from user where username = #{username}")
    User findByUsername(String username);
    //注册用户
    @Insert("insert into user(username, password) values(#{username}, #{password})")
    void register(String username, String password);
}
