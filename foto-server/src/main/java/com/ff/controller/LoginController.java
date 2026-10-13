package com.ff.controller;

import com.ff.constant.MessageConstant;
import com.ff.dto.LoginUserDto;
import com.ff.entity.User;
import com.ff.properties.JwtProperties;
import com.ff.result.Result;

import com.ff.service.LoginService;
import com.ff.service.UserService;
import com.ff.utils.JwtUtils;
import com.ff.utils.PasswordEncoderUtil;
import com.ff.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/Login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProperties jwtProperties;



    //根据用户名登录
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody LoginUserDto loginUserDto){

        String username=loginUserDto.getUsername();
        String password=loginUserDto.getPassword();
        User user = userService.getUserByUsername(username);
        if(user==null){
            //用户名不存在
            return Result.error(MessageConstant.USERNAME_NOT_EXIST);
        }

        if(!PasswordEncoderUtil.matches(password, user.getPassword())){
            //密码错误
            return Result.error(MessageConstant.PASSWORD_ERROR);
        }

        // 生成 JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        String token = JwtUtils.generateToken(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }


    //注册
    @PostMapping("/register")
    public Result<String> register(@RequestBody LoginUserDto loginUserDto)
    {
        String username=loginUserDto.getUsername();
        String password=loginUserDto.getPassword();


        //判断用户名是否存在
        User user = loginService.findByUsername(username);
        if(user!=null){
            return Result.error(MessageConstant.USERNAME_ALREADY_EXIST);
        }

        //对密码进行加密
        password= PasswordEncoderUtil.encode(password);


        //注册用户
        loginService.register(username, password);
        return Result.success("注册成功,请登录");
    }

}
