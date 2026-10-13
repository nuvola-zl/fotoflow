package org.example.fotoflow.controller;

import org.example.fotoflow.dto.PasswordDto;
import org.example.fotoflow.dto.RealNameDto;
import org.example.fotoflow.pojo.Result;
import org.example.fotoflow.pojo.User;
import org.example.fotoflow.pojo.Work;
import org.example.fotoflow.service.UserService;
import org.example.fotoflow.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.example.fotoflow.utils.AliOssUtil;
import java.util.Map;
@RestController
@RequestMapping("/space")
public class UserController {
    @Autowired
    private UserService userService;

    //获取用户的信息
    @GetMapping("/userInfo")
    public Result getUserInfo(@RequestHeader("token") String token) {
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long userId = ((Number) claims.get("userId")).longValue(); //  安全转换
        User user = userService.getUserById(userId);
        return Result.success(user);
    }

    //修改用户信息:用户名
    @PostMapping("/updateUsername")
    public Result updateUsername(@RequestHeader("token") String token,
                                 @RequestBody User user) {
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long userId = ((Number) claims.get("userId")).longValue();   //  安全转换
        userService.updateUsername(userId, user.getUsername());
        return Result.success();
    }


    //按钮一，去实名认证  1,实名信息回显
    // ====================== 1. 实名信息回显 ======================
    @GetMapping("/realName")
    public Result getRealName(@RequestHeader("token") String token) {
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long userId = ((Number) claims.get("userId")).longValue(); //  安全转换

        RealNameDto realNameDto = userService.getRealName(userId);
        return Result.success(realNameDto);
    }

    //点击按钮一之后界面上的按钮，修改用户实名信息
    // ====================== 2. 提交实名认证 ======================
    @PostMapping("/realName/update")
    public Result updateRealName(@RequestHeader("token") String token,
                                 @RequestBody RealNameDto realNameDto) {
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long userId = ((Number) claims.get("userId")).longValue(); //  安全转换

        // 非空判断
        if (realNameDto.getRealName() == null || realNameDto.getRealName().isEmpty()) {
            return Result.error("请输入真实姓名");
        }
        if (realNameDto.getIdCard() == null || realNameDto.getIdCard().isEmpty()) {
            return Result.error("请输入身份证号");
        }
        if (realNameDto.getPhone() == null || realNameDto.getPhone().isEmpty()) {
            return Result.error("请输入手机号");
        }

        // 身份证格式（18位）
        if (!realNameDto.getIdCard().matches("\\d{17}[0-9Xx]")) {
            return Result.error("身份证格式不正确");
        }

        // 手机号格式
        if (!realNameDto.getPhone().matches("^1[3-9]\\d{9}$")) {
            return Result.error("手机号格式不正确");
        }

        userService.updateRealName(userId, realNameDto);
        return Result.success("实名认证成功！");
    }


    //按钮二，去修改个人密码，此处不需要回显密码，需要输入原密码，然后输入新密码，确认新密码，最后点击修改按钮
    // ====================== 3. 修改密码 ======================
    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestHeader("token") String token,
                                 @RequestBody PasswordDto passwordDto) {
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long userId = ((Number) claims.get("userId")).longValue(); // ✅ 安全转换

        // 原密码校验
        String oldPwd = userService.getPassword(userId);
        if (!passwordDto.getOldPassword().equals(oldPwd)) {
            return Result.error("原密码错误");
        }

        // 新密码两次一致
        if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmPassword())) {
            return Result.error("新密码两次输入不一致");
        }

        userService.updatePassword(userId, passwordDto.getNewPassword());
        return Result.success("密码修改成功");
    }

    //此处可以添加功能，修改头像，此处需要修改user数据库中的avatarUrl字段
    //按钮三，修改头像
    @PostMapping("/updateAvatar")
    public Result updateAvatar(@RequestHeader("token") String token,
                               @RequestParam("file") MultipartFile file) throws Exception {
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long userId = ((Number) claims.get("userId")).longValue();

        String url = AliOssUtil.upload(file);
        userService.updateAvatar(userId, url);


        return Result.success(url);
    }

    //上传头像到阿里云OSS
    @PostMapping("/uploadAvatarAli")
    public Result uploadAvatarAli(@RequestParam("file") MultipartFile file) throws Exception {
        String url = AliOssUtil.upload(file);
        return Result.success(url);
    }

    //按钮四，查看我发布的图片
    // ====================== 4. 查看我发布的作品 ======================
    // ====================== 4. 查看我发布的作品 ======================

    //---------------------------------
    @GetMapping("/myWorks")
    public Result getMyWorks(@RequestHeader("token") String token,
                             @RequestParam Integer pageNum,
                             @RequestParam Integer pageSize) {
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long userId = ((Number) claims.get("userId")).longValue();

        // 获取作品数据
        Map<String, Object> workMap = userService.getWork(userId, pageNum, pageSize);
        return Result.success(workMap);
    }
    /**
     * 点击查看我发布的图片后，进入我发布的图片的主界面，此时可以点击查询到的图片，点击之后进入图片详情，可以进行一些操作
     */

    // 点击发布的作品中的某一个,进入详情页面,显示图片的详细信息
    @GetMapping("/myWorks/{workId}")
    public Result getWorkDetail(@PathVariable Long workId) {
        Work work = userService.getWorkById(workId);
        //此处要回显创建人的姓名
        User createUser = userService.getUserById(work.getUserId());
        work.setUsername(createUser.getUsername());

        //详情中主要展示1：title 2:content 3img_url 4:username 5:category 6:likeCount 7:collectCount 8:commentCount 9:createTime
        return Result.success(work);
    }

    //上传头像到阿里云OSS
    @PostMapping("/myWorks/upload")
    public Result uploadAvatar(@RequestParam("file") MultipartFile file) throws Exception {
        String url = AliOssUtil.upload(file);
        return Result.success(url);
    }

    //修改保存图片信息
    @PutMapping("/myWorks/update")
    public Result updateWork(@RequestHeader("token") String token,
                             @RequestBody Work work) {
        //获取用户id
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long userId = ((Number) claims.get("userId")).longValue(); // ✅ 安全转换
        work.setUserId(userId);

        userService.updateWork( work);
        return Result.success("图片信息成功更新");
    }

    //删除图片
    @DeleteMapping("/myWorks/{workId}")
    public Result deleteWork(@PathVariable Long workId) {
        userService.deleteWork(workId);
        return Result.success("图片删除成功");
    }


    //可添加功能，设为隐私，仅自己可看，此处要修改数据库
    //按钮五，查看我收藏的图片
    //此处不采用分页查询了，采用整页查询
    @GetMapping("/myCollects")
    public Result getMyCollects(@RequestHeader("token") String token) {
        // 从token中获取userId
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long userId = ((Number) claims.get("userId")).longValue(); // ✅ 安全转换


        Map<String, Object> workMap = userService.getCollects(userId);
        return Result.success(workMap);
    }


}