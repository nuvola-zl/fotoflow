package com.ff.service.impl;


import com.ff.constant.JwtClaimsConstant;

import com.ff.constant.MessageConstant;
import com.ff.dto.LoginUserDto;
import com.ff.dto.RiderLoginDto;
import com.ff.entity.Rider;
import com.ff.entity.User;
import com.ff.exception.BaseException;
import com.ff.exception.CodeException;
import com.ff.mapper.LoginMapper;
import com.ff.mapper.RiderMapper;
import com.ff.properties.JwtProperties;
import com.ff.result.Result;
import com.ff.service.LoginService;
import com.ff.utils.JwtUtils;
import com.ff.utils.PasswordEncoderUtil;
import com.ff.vo.RiderLoginVO;
import com.ff.vo.RiderVO;
import com.ff.vo.UserLoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {


    private final LoginMapper loginMapper;
    private final RiderMapper riderMapper;
    private final JwtProperties jwtProperties;


    /**
     * 用户登录
     * @param loginUserDto
     * @return
     */
    public UserLoginVO loginUser(LoginUserDto loginUserDto) {
        User user = loginMapper.findByUsername(loginUserDto.getUsername());
        if (user == null || !PasswordEncoderUtil.matches(loginUserDto.getPassword(), user.getPassword())) {
            throw new BaseException(CodeException.GENERAL, MessageConstant.USERNAME_AND_PASSWORD_ERROR);
        }
        //生成JWT Token
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtils.generateToken(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        return  UserLoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus())
                .token(token)
                .build();
    }

    /**
     * 用户注册
     */
    @Override
    @Transactional
    public void registerUser(LoginUserDto loginUserDto) {
        // 校验用户名是否存在
        User user = loginMapper.findByUsername(loginUserDto.getUsername());
        if (user != null) {
            throw new BaseException(CodeException.GENERAL, MessageConstant.USERNAME_ALREADY_EXIST);
        }
        //校验用户名和密码是否为空
        if (loginUserDto.getUsername() == null || loginUserDto.getUsername().length() < 2) {
            throw new BaseException(CodeException.PARAM_ERROR, MessageConstant.NAME_ERROR);
        }
        if (loginUserDto.getPassword() == null || loginUserDto.getPassword().length() < 6) {
            throw new BaseException(CodeException.PARAM_ERROR, MessageConstant.PASSWORD_ERROR);
        }
        //构建用户实体
        user = User.builder()
                .username(loginUserDto.getUsername())
                .password(PasswordEncoderUtil.encode(loginUserDto.getPassword()))
                .avatarUrl(null)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        //ALTER TABLE user ADD UNIQUE KEY uk_username (username);
        // 校验用户名是否已存在
        //  入库
        // 先创建唯一索引，再入库，避免并发下的重复键异常
        // 兜底：捕获唯一索引冲突（拦截并发下的 1%）
        try {
            loginMapper.registerUser(user);
        } catch (DuplicateKeyException e) {
            // Spring 包装后的重复键异常
            throw new BaseException(CodeException.GENERAL, MessageConstant.USERNAME_ALREADY_EXIST);
        }

    }

    /**
     * 骑手登录
     */
    @Override
    public RiderLoginVO loginRider(RiderLoginDto dto) {
        Rider rider = riderMapper.selectByPhone(dto.getPhone());
        // 统一提示，防止枚举攻击
        if (rider == null || !PasswordEncoderUtil.matches(dto.getPassword(), rider.getPassword())) {
            throw new BaseException(CodeException.GENERAL, MessageConstant.PHONE_AND_PASSWORD_ERROR);
        }


        //生成JWT Token
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.RIDER_ID, rider.getId());
        String token = JwtUtils.generateToken(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
       return  RiderLoginVO.builder()
                .id(rider.getId())
                .phone(rider.getPhone())
                .realName(rider.getRealName())
                .avatar(rider.getAvatar())
                .status(rider.getStatus())
                .rating(rider.getRating())
                .token(token)
                .build();
    }

    /**
     * 骑手注册
     */
    @Override
    @Transactional
    public void registerRider(RiderLoginDto dto) {
        // 0. 校验姓名
        if (dto.getRealName() == null || dto.getRealName().length() < 2) {
            throw new BaseException(CodeException.PARAM_ERROR, MessageConstant.NAME_ERROR);
        }
        // 1. 校验手机号
        if (dto.getPhone() == null || !dto.getPhone().matches("^1[3-9]\\d{9}$")) {
            throw new BaseException(CodeException.PARAM_ERROR, MessageConstant.PHONE_ERROR);
        }
        // 2. 校验密码
        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            throw new BaseException(CodeException.PARAM_ERROR, MessageConstant.PASSWORD_ERROR);
        }


        // 5. 手机号是否已注册
        Rider exist = riderMapper.selectByPhone(dto.getPhone());
        if (exist != null) {
            throw new BaseException(CodeException.GENERAL, MessageConstant.PHONE_ALREADY_EXIST);
        }

        // 6. 密码加密
        String encodePwd = PasswordEncoderUtil.encode(dto.getPassword());

        // 7. 构建骑手对象
        Rider rider = Rider.builder()
                .phone(dto.getPhone())
                .password(encodePwd)
                .realName(dto.getRealName())
                .avatar(null)
                .status(0)
                .rating(5.0)
                .totalOrders(0)
                .todayOrders(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        // 8. 入库
        // 先创建唯一索引，再入库，避免并发下的重复键异常
        //ALTER TABLE rider ADD UNIQUE KEY uk_phone (phone);
        // 兜底：捕获唯一索引冲突（拦截并发下的 1%）
        try {
            riderMapper.insert(rider);
        } catch (DuplicateKeyException e) {
            // Spring 包装后的重复键异常
            throw new BaseException(CodeException.GENERAL, MessageConstant.PHONE_ALREADY_EXIST);
        }
    }

}
