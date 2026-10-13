package com.ff.controller;

import com.ff.context.BaseContext;
import com.ff.dto.UserUpdateDto;
import com.ff.result.Result;
import com.ff.service.UserService;
import com.ff.utils.AliOssUtil;
import com.ff.vo.UserAddressVO;
import com.ff.vo.UserInfoVO;
import com.ff.vo.UserUpdateVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ff.constant.MessageConstant;

import java.util.List;

@RestController
@RequestMapping("/space")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final AliOssUtil aliOssUtil;

    /**
     * 获取用户的信息
     * @return
     */
    @GetMapping("/userInfo")
    public Result<UserInfoVO> getUserInfo() {
        return Result.success(userService.getUserById(BaseContext.getCurrentId()));
    }

    /**
     * 1.修改用户的信息的信息回显
     */
    @GetMapping("/showInfo")
    public Result<UserUpdateVO> showInfo() {
        return Result.success(userService.showInfo());
    }
    /**
     * 1.修改用户的信息,修改
     */
    @PostMapping("/updateInfo")
    public Result<String> updateInfo(@RequestBody @Valid UserUpdateDto userUpdateDto) {
        userService.updateUserInfo(userUpdateDto);
        return Result.success(MessageConstant.YES);
    }


}