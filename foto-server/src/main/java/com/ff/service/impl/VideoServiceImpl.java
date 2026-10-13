package com.ff.service.impl;

import com.ff.component.VideoDurationAsyncComponent;
import com.ff.constant.RedisKeyConstant;
import com.ff.context.BaseContext;
import com.ff.dto.VideoPublishDto;
import com.ff.entity.*;
import com.ff.exception.CollectSetException;
import com.ff.exception.LikeSetException;
import com.ff.mapper.VideoMapper;
import com.ff.result.Result;
import com.ff.constant.TableRedisMapperConstant;
import com.ff.service.VideoService;
import com.ff.vo.BarrageVO;
import com.ff.vo.VideoVO;
import jakarta.annotation.Resource;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class VideoServiceImpl implements VideoService {

    @Resource
    private VideoMapper videoMapper;

    @Autowired
    private RedissonClient redissonClient;


    //注入异步组件
    private final VideoDurationAsyncComponent durationComponent;

    public VideoServiceImpl(VideoDurationAsyncComponent durationComponent) {
        this.durationComponent = durationComponent;
    }

    /**
     * 通用点赞/收藏切换方法
     */
    private Boolean toggleCommon(
            Long videoId,
            String lockPrefix,       // 锁前缀
            String tableName,        // 表名
            String countColumn,      // 计数字段
            Class<? extends RuntimeException> exClass // 异常类
    ) {
        Long userId = BaseContext.getCurrentId();
        String lockKey = String.format(lockPrefix, userId, videoId);
        RLock lock = redissonClient.getLock(lockKey);

        // 尝试加锁
        boolean locked;
        try {
            locked = lock.tryLock(3, 30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (!locked) {
            try {
                throw exClass.getConstructor(String.class).newInstance("操作频繁，请稍后再试");
            } catch (Exception ex) {
                throw new RuntimeException("操作频繁");
            }
        }

        try {
            // 判断是否已存在
            int count = videoMapper.checkExist(tableName, videoId, userId);
            if (count == 0) {
                // 不存在 → 添加
                videoMapper.updateCount(countColumn, videoId, 1);
                videoMapper.toggleRelation(tableName, videoId, userId, 1);
                return true;
            } else {
                // 存在 → 删除
                videoMapper.updateCount(countColumn, videoId, -1);
                videoMapper.toggleRelation(tableName, videoId, userId, -1);
                return false;
            }
        } catch (Exception e) {
            try {
                throw exClass.getConstructor(String.class).newInstance("操作失败");
            } catch (Exception ex) {
                throw new RuntimeException("操作失败");
            }
        } finally {
            lock.unlock();
        }
    }


    //发布视频
    @Override
    public void publishVideo(VideoPublishDto videoPublishDto) {

        // 从token中获取userId
        Long userId = BaseContext.getCurrentId();

        // 2. 自动获取视频时长（秒）
        int duration = 0;
        // 4. 封装数据
        Video video = new Video();
        BeanUtils.copyProperties(videoPublishDto, video);
        video.setUserId(userId);
        video.setDuration(duration);

        //  保存到数据库
        videoMapper.publishVideo(video);

        // 异步获取视频时长
        durationComponent.asyncResolveDuration(video);


    }

    // 获取首页最新一条视频
    @Override
    public VideoVO getLatestOne() {

        // 1. 获取当前登录用户ID
        Long userId = BaseContext.getCurrentId();

        // 2. 获取视频
        Video video = videoMapper.getLatestOne();

        // 3. 查询当前用户 是否点赞 + 是否收藏
        boolean isLike = videoMapper.checkExist(TableRedisMapperConstant.USER_VIDEO_LIKE, video.getId(), userId) != 0;
        boolean isCollect = videoMapper.checkExist(TableRedisMapperConstant.USER_VIDEO_COLLECT, video.getId(), userId) != 0;

        // 4. 塞进 video 对象
        video.setIsLike(isLike);
        video.setIsCollect(isCollect);
        VideoVO videoVO = new VideoVO();
        BeanUtils.copyProperties(video, videoVO);
        return videoVO;
    }

    // 发送弹幕
    @Override
    public void sendBarrage(Barrage barrage,Long videoId) {
        // 从token中获取userId
        Long userId = BaseContext.getCurrentId();
        barrage.setUserId(userId);
        barrage.setVideoId(videoId);
        videoMapper.sendBarrage(barrage);
    }

    // 获取视频弹幕
    @Override
    public List<BarrageVO> getBarrages(Long videoId) {
        return videoMapper.getBarrages(videoId);
    }

    // 点赞
    @Override
    public Boolean updateLikeAll(Long id) {
        return toggleCommon(
                id,
                RedisKeyConstant.LOCK_LIKE,
                TableRedisMapperConstant.USER_VIDEO_LIKE,
                TableRedisMapperConstant.LIKE_COUNT_KEY,
                LikeSetException.class
        );
    }

    // 收藏
    @Override
    public Boolean updateCollectAll(Long id) {
        return toggleCommon(
                id,
                RedisKeyConstant.LOCK_COLLECT,
                TableRedisMapperConstant.USER_VIDEO_COLLECT,
                TableRedisMapperConstant.COLLECT_COUNT_KEY,
                CollectSetException.class
        );
    }

    // 点赞/收藏 通用方法

//    @Override
//    public Boolean updateLikeAll(Long id){
//
//        // 从token中获取userId
//        Long userId = BaseContext.getCurrentId();
//
//        // 每个用户 + 每个视频 → 独立锁
//        String lockKey = String.format(RedisKeyConstant.LOCK_LIKE, userId, id);
//        RLock lock = redissonClient.getLock(lockKey);
//        boolean locked = false;
//        try {
//            locked = lock.tryLock(3, 30, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        if (!locked) {
//            throw new LikeSetException("操作频繁，请稍后再试");
//        }
//
//        try {
//            //先判断是否已经点赞了
//            int count = videoMapper.checkExist(TableRedisMapperConstant.USER_VIDEO_LIKE, id, userId);
//            if(count==0) {
//                //没点赞过，点赞数增加1
//                // 点赞成功后，刷新点赞按钮，显示点赞数增加1
//                //点赞按钮的点赞图标改变为已点赞图标
//                videoMapper.updateCount(TableRedisMapperConstant.LIKE_COUNT_KEY, id, 1);
//                videoMapper.toggleRelation(TableRedisMapperConstant.USER_VIDEO_LIKE, id, userId, 1);
//                return true;
//            }
//            else {
//                //点赞过，点赞数减少1
//                // 点赞成功后，刷新点赞按钮，显示点赞数减少1
//                //点赞按钮的点赞图标改变为未点赞图标
//                videoMapper.updateCount(TableRedisMapperConstant.LIKE_COUNT_KEY, id, -1);
//                videoMapper.toggleRelation(TableRedisMapperConstant.USER_VIDEO_LIKE, id, userId, -1);
//                return false;
//            }
//        } catch (Exception e) {
//            throw new LikeSetException("点赞失败");
//        } finally {
//            lock.unlock(); // 解锁
//        }
//    }
//
//    public Boolean updateCollectAll(Long id){
//        // 从token中获取userId
//        Long userId = BaseContext.getCurrentId();
//
//        // 每个用户 + 每个视频 → 独立锁
//        String lockKey = String.format(RedisKeyConstant.LOCK_COLLECT, userId, id);
//        RLock lock = redissonClient.getLock(lockKey);
//        boolean locked = false;
//        try {
//            locked = lock.tryLock(3, 30, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        if (!locked) {
//            throw new CollectSetException("操作频繁，请稍后再试");
//        }
//
//        try {
//            //先判断是否已经收藏了
//            int count = videoMapper.checkExist(TableRedisMapperConstant.USER_VIDEO_COLLECT, id, userId);
//            if (count == 0) {
//                //没收藏过，收藏数增加1
//                videoMapper.updateCount(TableRedisMapperConstant.COLLECT_COUNT_KEY, id, 1);
//                videoMapper.toggleRelation(TableRedisMapperConstant.USER_VIDEO_COLLECT, id, userId, 1);
//                return true;
//            } else {
//                //收藏过，收藏数减少1
//                videoMapper.updateCount(TableRedisMapperConstant.COLLECT_COUNT_KEY, id, -1);
//                videoMapper.toggleRelation(TableRedisMapperConstant.USER_VIDEO_COLLECT, id, userId, -1);
//                return false;
//            }
//        } catch (Exception e) {
//            throw new CollectSetException("收藏失败");
//        } finally {
//            lock.unlock(); // 解锁
//        }
//    }


    // 获取视频评论
    @Override
    public List<Comment> listCommentsByWorkId(Long id) {
        return videoMapper.listCommentsByWorkId(id);
    }

    // 添加评论
    @Override
    public void addComment(Comment comment) {
        videoMapper.addComment(comment);
    }

    // 更新视频评论数
    @Override
    public void addCommentNum(Long id) {
        videoMapper.addCommentNum(id);
    }

    //根据作品id查询作者
    @Override
    public Video getVideoById(Long id) {
        return videoMapper.getVideoById(id);
    }

    //根据用户名查询用户
    @Override
    public User getUserByUsername(String username) {
        return videoMapper.getUserByUsername(username);
    }

    //添加信息
    @Override
    public void addAtMessage(Message msg) {
        videoMapper.addAtMessage(msg);
    }

    @Override
    public void updateById(Video video) {
        videoMapper.updateById(video);
    }
}
