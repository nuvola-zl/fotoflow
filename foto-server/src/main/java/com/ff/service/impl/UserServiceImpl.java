package com.ff.service.impl;
import com.ff.context.BaseContext;
import com.ff.dto.UserUpdateDto;
import com.ff.exception.BaseException;
import com.ff.exception.CodeException;
import com.ff.mapper.UserAddressMapper;
import com.ff.utils.PasswordEncoderUtil;
import com.ff.entity.User;
import com.ff.mapper.UserMapper;
import com.ff.service.UserService;
import com.ff.vo.UserInfoVO;
import com.ff.vo.UserUpdateVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserAddressMapper userAddressMapper;

    /**
     * 根据用户id查询用户信息
     * 可以用于前端pinia状态管理，更新用户信息后，需要刷新pinia状态管理
     * @param userId
     * @return
     */
    @Override
    public UserInfoVO getUserById(Long userId) {
        return userMapper.selectUserById(userId);
    }

   @Override
    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    /**
     * 1.修改用户的信息的信息回显
     */
    @Override
    public UserUpdateVO showInfo() {
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.selectById(userId);
        UserUpdateVO userUpdateVO = new UserUpdateVO();
        userUpdateVO.setId(user.getId());
        userUpdateVO.setUsername(user.getUsername());
        userUpdateVO.setAvatarUrl(user.getAvatarUrl());
        return userUpdateVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfo(UserUpdateDto dto) {
        User user = userMapper.selectById(dto.getId());
        if (user == null) {
            throw new BaseException(CodeException.NOT_FOUND, "用户不存在");
        }

        // 更新用户名
        if (dto.getUsername() != null) {
            user.setUsername(dto.getUsername());
        }

        // 更新头像
        if (dto.getAvatarUrl() != null) {
            user.setAvatarUrl(dto.getAvatarUrl());
        }

        // 密码修改（只有填了才执行）
        boolean needChangePwd = dto.getOldPassword() != null && !dto.getOldPassword().isEmpty()
                && dto.getPassword() != null && !dto.getPassword().isEmpty();

        if (needChangePwd) {
            if (!dto.getPassword().equals(dto.getConfirmPassword())) {
                throw new BaseException(CodeException.PARAM_ERROR, "新密码和确认密码不一致");
            }
            if (!PasswordEncoderUtil.matches(dto.getOldPassword(), user.getPassword())) {
                throw new BaseException(CodeException.PARAM_ERROR, "旧密码错误");
            }
            user.setPassword(PasswordEncoderUtil.encode(dto.getPassword()));
        }

        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateInfo(user);
    }

}
