package org.example.fotoflow.controller;

import org.example.fotoflow.dto.LoginUserDto;
import org.example.fotoflow.pojo.Result;
import org.example.fotoflow.pojo.User;
import org.example.fotoflow.service.LoginService;
import org.example.fotoflow.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/Login")
public class LoginController {

    @Autowired
    private LoginService loginService;


    //根据用户名登录
    @PostMapping("/login")
    public Result login(@RequestBody LoginUserDto loginUserDto){

        String username=loginUserDto.getUsername();
        String password=loginUserDto.getPassword();
        User user = loginService.findByUsername(username);
        if(user==null){
            return Result.error("用户名不存在");
        }

        if(!user.getPassword().equals(password)){
            return Result.error("密码错误");
        }
        // 生成 JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        String token = JwtUtils.generateToken(claims);

        return Result.success(token);
    }


    //注册
    @PutMapping("/register")
    public Result register(@RequestBody LoginUserDto loginUserDto)
    {
        String username=loginUserDto.getUsername();
        String password=loginUserDto.getPassword();
        //判断用户名是否存在
        User user = loginService.findByUsername(username);
        if(user!=null){
            return Result.error("用户名已存在");
        }
        //注册用户
        loginService.register(username, password);
        return Result.success("注册成功,请登录");
    }

}
