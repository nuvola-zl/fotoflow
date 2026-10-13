package com.ff.controller;

import com.ff.context.BaseContext;
import com.ff.dto.PasswordDto;
import com.ff.dto.RealNameDto;
import com.ff.dto.UserUpdateDto;
import com.ff.utils.PasswordEncoderUtil;
import com.ff.vo.RealNameVO;
import com.ff.entity.User;
import com.ff.entity.Work;
import com.ff.result.Result;
import com.ff.service.UserService;
import com.ff.utils.AliOssUtil;
import com.ff.vo.UserInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.ff.constant.MessageConstant;

import java.util.Map;
import java.util.UUID;
@RestController
@RequestMapping("/space")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AliOssUtil aliOssUtil;

    //获取用户的信息
    @GetMapping("/userInfo")
    public Result<UserInfoVO> getUserInfo() {
        return Result.success(userService.getUserById(BaseContext.getCurrentId()));
    }

//    //修改用户信息
//    @PostMapping("/updateUserInfo")
//    public Result<String> updateUsername(@RequestBody UserUpdateDto userUpdateDto) {
//
//        userService.updateUserInfo(userUpdateDto);
//        return Result.success(MessageConstant.USERNAME_UPDATE_SUCCESS);
//    }
//
//
//    //按钮一，去实名认证  1,实名信息回显
//    @GetMapping("/realName")
//    public Result<RealNameVO> getRealName() {
//        Long userId = BaseContext.getCurrentId(); //  安全转换
//
//        RealNameVO realNameVO = userService.getRealName(userId);
//        return Result.success(realNameVO);
//    }
//
//    //点击按钮一之后界面上的按钮，修改用户实名信息
//    @PostMapping("/realName/update")
//    public Result<String> updateRealName(@RequestBody RealNameDto realNameDto) {
//
//        // 从上下文获取当前用户ID
//        Long userId = BaseContext.getCurrentId();
//
//        // 非空判断
//        if (realNameDto.getRealName() == null || realNameDto.getRealName().isEmpty()) {
//            return Result.error("请输入真实姓名");
//        }
//        if (realNameDto.getIdCard() == null || realNameDto.getIdCard().isEmpty()) {
//            return Result.error("请输入身份证号");
//        }
//        if (realNameDto.getPhone() == null || realNameDto.getPhone().isEmpty()) {
//            return Result.error("请输入手机号");
//        }
//
//        // 身份证格式（18位）
//        if (!realNameDto.getIdCard().matches("\\d{17}[0-9Xx]")) {
//            return Result.error("身份证格式不正确");
//        }
//
//        // 手机号格式
//        if (!realNameDto.getPhone().matches("^1[3-9]\\d{9}$")) {
//            return Result.error("手机号格式不正确");
//        }
//
//        userService.updateRealName(userId, realNameDto);
//        return Result.success("实名认证成功！");
//    }
//
//
//    //按钮二，去修改个人密码，此处不需要回显密码，需要输入原密码，然后输入新密码，确认新密码，最后点击修改按钮
//    // ====================== 3. 修改密码 ======================
//    @PostMapping("/updatePassword")
//    public Result<String> updatePassword(@RequestBody PasswordDto passwordDto) {
//
//        // 从上下文获取当前用户ID
//        Long userId = BaseContext.getCurrentId(); //  安全转换
//
//        //获取原密码
//        String oldPwd = userService.getPassword(userId);
//
//        //密码比较，判断原密码是否正确
//        if (!PasswordEncoderUtil.matches(passwordDto.getOldPassword(), oldPwd)) {
//            return Result.error(MessageConstant.PASSWORD_ERROR);
//        }
//
//        // 新密码两次一致
//        if (!PasswordEncoderUtil.matches(passwordDto.getNewPassword(), passwordDto.getConfirmPassword())) {
//            return Result.error(MessageConstant.PASSWORD_NOT_EQUAL);
//        }
//
//        userService.updatePassword(userId, PasswordEncoderUtil.encode(passwordDto.getNewPassword()));
//        return Result.success(MessageConstant.PASSWORD_UPDATE_SUCCESS);
//    }
//
//    //此处可以添加功能，修改头像，此处需要修改user数据库中的avatarUrl字段
//    //按钮三，修改头像
//    @PostMapping("/updateAvatar")
//    public Result<String> updateAvatar(@RequestParam("file") MultipartFile file) throws Exception {
//        Long userId = BaseContext.getCurrentId(); //  安全转换
//
//        //原始文件名
//        String originalFilename=file.getOriginalFilename();
//
//        //截取文件名后缀
//        String extension= null;
//        if (originalFilename != null) {
//            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//        }
//
//        //构造新文件名
//        String objectName= UUID.randomUUID().toString()+extension;
//
//        String filePath=aliOssUtil.upload(file.getBytes(),objectName);
//
//        userService.updateAvatar(userId, filePath);
//
//
//        return Result.success(filePath);
//    }
//
//    @GetMapping("/myWorks")
//    public Result<Map<String, Object>> getMyWorks(@RequestParam Integer pageNum,
//                             @RequestParam Integer pageSize) {
//        Long userId = BaseContext.getCurrentId(); //  安全转换
//
//        // 获取作品数据
//        Map<String, Object> workMap = userService.getWork(userId, pageNum, pageSize);
//        return Result.success(workMap);
//    }
//    /**
//     * 点击查看我发布的图片后，进入我发布的图片的主界面，此时可以点击查询到的图片，点击之后进入图片详情，可以进行一些操作
//     */

//    // 点击发布的作品中的某一个,进入详情页面,显示图片的详细信息
//    @GetMapping("/myWorks/{workId}")
//    public Result<Work> getWorkDetail(@PathVariable Long workId) {
//        Work work = userService.getWorkById(workId);
//        //此处要回显创建人的姓名
//        User createUser = userService.getUserById(work.getUserId());
//        work.setUsername(createUser.getUsername());
//
//        //详情中主要展示1：title 2:content 3img_url 4:username 5:category 6:likeCount 7:collectCount 8:commentCount 9:createTime
//        return Result.success(work);
//    }

//    //修改保存图片信息
//    @PutMapping("/myWorks/update/{workId}")
//    public Result<String> updateWork(@PathVariable Long workId, @RequestBody Work work) {
//        //获取用户id
//        Long userId = BaseContext.getCurrentId();
//
//        //根据workId查询作品
//        Work workById = userService.getWorkById(workId);
//        //判断作品是否存在
//        if(workById==null){
//            return Result.error(MessageConstant.WORK_NOT_EXIST);
//        }
//        //判断是不是当前用户发布的图片
//        if(!workById.getUserId().equals(userId)){
//            return Result.error(MessageConstant.NOT_YOUR_WORK);
//        }
//
//        work.setUserId(userId);
//
//        userService.updateWork(work);
//        return Result.success(MessageConstant.WORK_UPDATE_SUCCESS);
//    }
//
//    //删除图片
//    @DeleteMapping("/myWorks/{workId}")
//    public Result<String> deleteWork(@PathVariable Long workId) {
//        //获取当前用户id
//        Long userId = BaseContext.getCurrentId();
////        //判断是不是当前用户发布的图片
////        if(!work.getWorkId().equals(workId)){
////            return Result.error(MessageConstant.NOT_YOUR_WORK);
////        }
//        userService.deleteWork(workId);
//        return Result.success(MessageConstant.WORK_DELETE_SUCCESS);
//    }
//
//
//    //可添加功能，设为隐私，仅自己可看，此处要修改数据库
//    //按钮五，查看我收藏的图片
//    //此处不采用分页查询了，采用整页查询
//    @GetMapping("/myCollects")
//    public Result<Map<String, Object>> getMyCollects() {
//        // 从token中获取userId
//        Long userId = BaseContext.getCurrentId(); //  安全转换
//
//
//        Map<String, Object> workMap = userService.getCollects(userId);
//        return Result.success(workMap);
//    }

}