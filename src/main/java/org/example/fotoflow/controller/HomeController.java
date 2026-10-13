package org.example.fotoflow.controller;
import org.example.fotoflow.dto.WorkDto;
import org.example.fotoflow.pojo.*;
import org.example.fotoflow.service.HomeService;
import org.example.fotoflow.utils.AliOssUtil;
import org.example.fotoflow.utils.JwtUtils;
import org.example.fotoflow.utils.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
//安全转换,原因是jwt解析出来的是integer类型的要转换为long类型的
/**
 * 主界面控制器
 */
@RestController
@RequestMapping("/home")
public class HomeController {


    @Autowired
    private HomeService homeService;

    //----------------------------------------------
    //发布自己的图片
    @PostMapping("/addMyWork")
    public Result addMyWork(@RequestHeader("token") String token, @RequestBody WorkDto workDto) {

        //先判断图片是否上传
        if(workDto.getImgList() == null || workDto.getImgList().isEmpty()){
            return Result.error("请上传作品图片");
        }
        // 从token中获取userId
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long userId = ((Number) claims.get("userId")).longValue();
        //--------------------------------
        //通过id查询帐号状态，如果没有实名认证，或者账号在封禁状态不能发表
//        if(homeService.getUserById(userId).getIsRealName()==0)
//        {
//            return Result.error("账号没有实名认证,不能发表，请去实名认证");
//        }
        if(homeService.getUserById(userId).getStatus()==1)
        {
            return  Result.error("账号状态异常，账号被封禁");
        }

        //-----------------------------------
        homeService.addWork(workDto, userId);
        return Result.success("发布成功");

    }

    //上传图片
    @PostMapping("/uploadImg")
    public Result upload(@RequestParam("file") MultipartFile file) throws Exception {
        String url = AliOssUtil.upload(file);
        return Result.success(url);
    }

    //逐条查找作品

    //没有审核的作品，不能展示
    @GetMapping("/workList")
    public Result workList() {
        Work work = homeService.listWorksByPage();


        return Result.success(work);
    }

    //-------------------------------
    //多图片展示，一次展示五条数据，实现无限向上滑动
    @GetMapping("/workImageList")
    public Result workImageList(@RequestParam(defaultValue = "1") Integer pageNum,@RequestHeader("token") String token) {
        // 从token中获取userId
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long userId = ((Number) claims.get("userId")).longValue(); // ✅ 安全转换
        List<Work> list = homeService.getWorkList(pageNum);
        // 4. 查询当前用户 是否点赞 + 是否收藏
        //用于判断进入详情的时候，点赞和收藏图标状态，是否应该显示已点赞图标或已收藏图标，高亮与否

        for (Work work : list) {
            User publisher = homeService.getUserById(work.getUserId());
            work.setUsername(publisher.getUsername());
            work.setAvatar(publisher.getAvatarUrl());

            boolean isLike = homeService.likeIsExist(work.getId(), userId) != null;
            boolean isCollect = homeService.collectIsExist(work.getId(), userId) != null;
            work.setIsLike(isLike);
            work.setIsCollect(isCollect);
        }
        return Result.success(list);
    }
    /**
     * 图片详情展示，根据图片id查询图片详情，点击之后，如同抖音一般左侧有个按钮是简介，右边是评论按钮，点击之后转到评论页面
     *
     * @param id
     * @return
     */
    //图片详情展示，点击，默认是简介
    @GetMapping("/workDetail/{id}")
    public Result workDetail(@PathVariable Long id,@RequestHeader("token") String token) {

        // 从token中获取userId
        Map<String, Object> claims = JwtUtils.parseToken(token);
        System.out.println( claims);

        Long userId = ((Number) claims.get("userId")).longValue(); // ✅ 安全转换

        // 4. 查询当前用户 是否点赞 + 是否收藏
        //用于判断进入详情的时候，点赞和收藏图标状态，是否应该显示已点赞图标或已收藏图标，高亮与否
        boolean isLike = homeService.likeIsExist(id, userId) != null;
        boolean isCollect = homeService.collectIsExist(id, userId) != null;
        Work work = homeService.workDetail(id);
        User publisher = homeService.getUserById(work.getUserId());
            work.setUsername(publisher.getUsername());
            work.setAvatar(publisher.getAvatarUrl());
        // 5. 把点赞状态和收藏状态添加上去
        work.setIsLike(isLike);
        work.setIsCollect(isCollect);
        return Result.success(work);
    }


    // 点赞
    @PostMapping("/workDetail/like/{id}")
    public Result like(@PathVariable Long id,@RequestHeader("token") String token) {

        // 从token中获取userId
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long userId = ((Number) claims.get("userId")).longValue(); //  安全转换

        //先判断是否已经点赞了

        Like like = homeService.likeIsExist(id,userId);
        if(like==null) {
        //没点赞过，点赞数增加1
            // 点赞成功后，刷新点赞按钮，显示点赞数增加1
            //点赞按钮的点赞图标改变为已点赞图标
            homeService.updateWorkLike(id,0);
        homeService.updateLike(id, userId,0);
        return Result.success(true);
        }
        else {
            //点赞过，点赞数减少1
            // 点赞成功后，刷新点赞按钮，显示点赞数减少1
            //点赞按钮的点赞图标改变为未点赞图标
            homeService.updateWorkLike(id,1);
            homeService.updateLike(id, userId,1);
            return Result.success(false);
        }

    }


    // 收藏
    @PostMapping("/workDetail/collect/{id}")
    public Result collect(@PathVariable Long id,@RequestHeader("token") String token) {
        // 从token中获取userId
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long userId = ((Number) claims.get("userId")).longValue(); // 安全转换
        //先判断是否已经收藏了
        Collect collect = homeService.collectIsExist(id,userId);
        if(collect==null) {
            //没收藏过，收藏数增加1

            //TODO更新work表的collectNum
            homeService.updateWorkCollect(id,0);
            homeService.updateCollect(id, userId,0);
            return Result.success(true);
        }
        else {
            //收藏过，收藏数减少1
            //TODO更新work表的collectNum
            homeService.updateWorkCollect(id,1);
            homeService.updateCollect(id, userId,1);
            return Result.success(false);
        }

    }

    // 评论
    //点击评论按钮，先回显所有评论，再显示评论框
    //作品id
    @GetMapping("/workDetail/comments/{id}")
    public Result comment(@PathVariable Long id) {
        List<Comment> comments = homeService.listCommentsByWorkId(id);
        return Result.success(comments);
    }
    //在评论框中输入评论内容，点击发送生成评论
    @PostMapping("/workDetail/comment/{id}")
    public Result addComment(@RequestHeader("token") String token,@PathVariable Long id, @RequestBody Comment comment) {
        // 从token中获取userId
        Map<String, Object> claims = JwtUtils.parseToken(token);
        Long userId = ((Number) claims.get("userId")).longValue(); //  安全转换
        comment.setWorkId(id);
        comment.setUserId(userId);
        homeService.addComment(comment);

        //增加评论数
        homeService.addCommentNum(id);

        // ==================== 只加这一行 ====================
        // 根据作品ID查作者ID → 推送给作者
        Work work = homeService.getWorkById(id);
        WebSocketServer.sendToUser(work.getUserId(), "newComment");
        // ===================================================
        return Result.success("评论成功");
        //之后刷新品论界面，显示最新的评论
    }

    //此处可以增加功能删除自己的评论

    //还可以添加给评论点赞

    //还可以实现给别人的评论回复

    //此处还可以实现给别人私信功能

}