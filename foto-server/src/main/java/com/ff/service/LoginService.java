package com.ff.service;


import com.ff.dto.LoginUserDto;
import com.ff.dto.RiderLoginDto;
import com.ff.entity.User;
import com.ff.vo.RiderLoginVO;
import com.ff.vo.RiderVO;
import com.ff.vo.UserLoginVO;

public interface LoginService {
    /**
     * 用户登录
     */
    UserLoginVO loginUser(LoginUserDto loginUserDto);

    /**
     * 用户注册
     */
    void registerUser(LoginUserDto loginUserDto);

    /**
     * 骑手登录
     */
    RiderLoginVO loginRider(RiderLoginDto dto);

    /**
     * 骑手注册
     */
    void registerRider(RiderLoginDto dto);
}
