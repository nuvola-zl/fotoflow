package com.ff.service.impl;
import com.ff.component.VideoDurationAsyncComponent;
import com.ff.constant.RedisKeyConstant;
import com.ff.context.BaseContext;
import com.ff.dto.VideoPublishDto;
import com.ff.entity.*;
import com.ff.exception.BaseException;
import com.ff.exception.CodeException;
import com.ff.mapper.VideoMapper;
import com.ff.service.VideoService;
import com.ff.vo.BarrageVO;
import com.ff.vo.VideoVO;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


/**
 * 视频业务实现类
 * 功能：视频发布、点赞、收藏、弹幕、查询、@消息等
 * 采用 Spring 官方推荐：构造器注入
 */
@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    // 视频数据库操作层
    private final VideoMapper videoMapper;

    // 分布式锁（Redisson）
    private final RedissonClient redissonClient;

    // 异步解析视频时长组件
    private final VideoDurationAsyncComponent durationComponent;

    // ===================== 分布式锁常量 =====================
    // 获取锁等待时间：3秒（3秒内抢不到锁，直接返回操作频繁）
    private static final long LOCK_WAIT_SECONDS = 3;
    // 锁自动释放时间：30秒（防止死锁）
    private static final long LOCK_LEASE_SECONDS = 30;

    // ==================== 点赞功能（独立实现，无动态SQL） ====================
    /**
     * 点赞 / 取消点赞
     * 带分布式锁 + 事务，高并发安全
     * @return true=点赞成功 false=取消点赞
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 事务：任何异常全部回滚
    public Boolean updateLikeAll(Long videoId) {
        // 1. 获取当前登录用户ID
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            throw new BaseException(CodeException.UNAUTHORIZED, "用户未登录");
        }

        // 2. 构造分布式锁key（每个用户+视频一把锁）
        String lockKey = String.format(RedisKeyConstant.LOCK_LIKE, userId, videoId);
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;

        try {
            // 3. 尝试获取锁
            locked = lock.tryLock(LOCK_WAIT_SECONDS, LOCK_LEASE_SECONDS, TimeUnit.SECONDS);
            if (!locked) {
                throw new BaseException(CodeException.GENERAL, "操作频繁，请稍后再试");
            }

            // 4. 查询是否已经点过赞
            int count = videoMapper.checkLikeExist(videoId, userId);
            if (count == 0) {
                // 未点赞 → 点赞数+1，插入点赞记录
                videoMapper.updateLikeCount(videoId, 1);
                videoMapper.insertLike(videoId, userId);
                return true;
            } else {
                // 已点赞 → 点赞数-1，删除点赞记录
                videoMapper.updateLikeCount(videoId, -1);
                videoMapper.deleteLike(videoId, userId);
                return false;
            }
        } catch (InterruptedException e) {
            // 线程中断处理
            Thread.currentThread().interrupt();
            throw new BaseException(CodeException.GENERAL, "操作被中断");
        } finally {
            // 【安全解锁】只有拿到锁 & 当前线程持有锁，才解锁
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    // ==================== 收藏功能（独立实现，无动态SQL） ====================
    /**
     * 收藏 / 取消收藏
     * 结构同点赞，高并发安全
     * @return true=收藏成功 false=取消收藏
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCollectAll(Long videoId) {
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            throw new BaseException(CodeException.UNAUTHORIZED, "用户未登录");
        }

        String lockKey = String.format(RedisKeyConstant.LOCK_COLLECT, userId, videoId);
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;

        try {
            locked = lock.tryLock(LOCK_WAIT_SECONDS, LOCK_LEASE_SECONDS, TimeUnit.SECONDS);
            if (!locked) {
                throw new BaseException(CodeException.GENERAL, "操作频繁，请稍后再试");
            }

            // 查询是否已收藏
            int count = videoMapper.checkCollectExist(videoId, userId);
            if (count == 0) {
                // 未收藏 → 收藏数+1，插入记录
                videoMapper.updateCollectCount(videoId, 1);
                videoMapper.insertCollect(videoId, userId);
                return true;
            } else {
                // 已收藏 → 收藏数-1，删除记录
                videoMapper.updateCollectCount(videoId, -1);
                videoMapper.deleteCollect(videoId, userId);
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BaseException(CodeException.GENERAL, "操作被中断");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    // ==================== 发布视频 ====================
    /**
     * 发布视频
     * 带事务，异步解析视频时长，不阻塞接口
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishVideo(VideoPublishDto dto) {
        // 登录校验
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            throw new BaseException(CodeException.UNAUTHORIZED, "用户未登录");
        }

        // 对象拷贝：dto → 实体类
        Video video = new Video();
        BeanUtils.copyProperties(dto, video);

        // 补充字段
        video.setUserId(userId);
        video.setDuration(0);  // 时长初始0，异步更新

        // 保存数据库
        videoMapper.publishVideo(video);

        // 异步解析视频时长（不影响发布速度）
        durationComponent.asyncResolveDuration(video);
    }

    // ==================== 获取首页最新一条视频 ====================
    /**
     * 获取首页随机一条视频（已审核）
     * 并返回当前用户的 点赞/收藏 状态
     */
    @Override
    @Transactional(readOnly = true)
    public VideoVO getLatestOne() {
        Long userId = BaseContext.getCurrentId();

        // ===================== 高性能随机查询（秒杀 RAND()） =====================
        // 1. 获取最大视频ID
        Long maxId = videoMapper.getMaxVideoId();
        if (maxId == null || maxId <= 0) {
            throw new BaseException(CodeException.NOT_FOUND, "暂无视频");
        }

        // 2. 生成 1 ~ maxId 之间的随机数
        long randomId = ThreadLocalRandom.current().nextLong(1, maxId + 1);

        // 3. 快速查询（毫秒级！）
        Video video = videoMapper.getRandomVideo(randomId);

        // 4. 兜底：防止随机到空洞（被删除的数据）
        if (video == null) {
            video = videoMapper.getRandomVideoFallback(randomId);
        }

        // 5. 最终还是空，抛异常
        if (video == null) {
            throw new BaseException(CodeException.NOT_FOUND, "暂无视频");
        }

        // 6. 查询当前用户是否点赞、收藏（游客默认 false）
        boolean isLike = false;
        boolean isCollect = false;

        if (userId != null) {
            isLike = videoMapper.checkLikeExist(video.getId(), userId) != 0;
            isCollect = videoMapper.checkCollectExist(video.getId(), userId) != 0;
        }

        video.setIsLike(isLike);
        video.setIsCollect(isCollect);

        // 7. 转换VO返回
        VideoVO vo = new VideoVO();
        BeanUtils.copyProperties(video, vo);
        return vo;
    }

    // ==================== 发送弹幕 ====================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendBarrage(Barrage barrage, Long videoId) {
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            throw new BaseException(CodeException.UNAUTHORIZED, "用户未登录");
        }

        // 设置用户ID、视频ID
        barrage.setUserId(userId);
        barrage.setVideoId(videoId);

        // 保存弹幕
        videoMapper.sendBarrage(barrage);
    }

    // ==================== 获取视频弹幕列表 ====================
    @Override
    public List<BarrageVO> getBarrages(Long videoId) {
        return videoMapper.getBarrages(videoId);
    }

}