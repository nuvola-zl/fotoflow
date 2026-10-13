package com.ff.controller;

import com.ff.constant.JwtClaimsConstant;
import com.ff.constant.MessageConstant;
import com.ff.dto.LoginUserDto;
import com.ff.dto.RiderLoginDto;
import com.ff.entity.User;
import com.ff.properties.JwtProperties;
import com.ff.result.Result;

import com.ff.service.LoginService;
import com.ff.service.UserService;
import com.ff.utils.JwtUtils;
import com.ff.utils.PasswordEncoderUtil;
import com.ff.vo.RiderLoginVO;
import com.ff.vo.RiderVO;
import com.ff.vo.UserLoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/Login")
@RequiredArgsConstructor
public class LoginController {


    private final LoginService loginService;

    private final UserService userService;

    private final JwtProperties jwtProperties;



    //根据用户名登录
    @PostMapping("/login")
    public Result<UserLoginVO> loginUser(@RequestBody LoginUserDto loginUserDto){
        return Result.success(loginService.loginUser(loginUserDto));
    }


    //注册
    @PostMapping("/register")
    public Result<String> registerUser(@RequestBody LoginUserDto loginUserDto)
    {
        //注册用户
        loginService.registerUser(loginUserDto);
        return Result.success("注册成功,请登录");
    }

        /** 登录 */
    @PostMapping("/rider/login")
    public Result<RiderLoginVO> loginRider(@RequestBody RiderLoginDto dto) {
        return Result.success(loginService.loginRider(dto));
    }

    /**注册**/
    @PostMapping("/rider/register")
    public Result<String> registerRider(@RequestBody RiderLoginDto dto) {
        loginService.registerRider(dto);
        return Result.success("注册成功,请登录");
    }


}
