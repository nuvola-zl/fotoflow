package com.ff.service.impl;


import com.ff.entity.User;
import com.ff.mapper.LoginMapper;
import com.ff.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private LoginMapper loginMapper;
    //根据用户名查找用户
    @Override
    public User findByUsername(String username) {


        return  loginMapper.findByUsername(username);

    }
    //注册用户
    @Override
    public void register(String username, String password) {
        loginMapper.register(username, password);
    }
}
