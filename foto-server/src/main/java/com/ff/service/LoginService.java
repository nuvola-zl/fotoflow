package com.ff.service;


import com.ff.entity.User;

public interface LoginService {
    //根据用户名查找用户
    User findByUsername(String username);

    //注册用户
    void register(String username, String password);
}
