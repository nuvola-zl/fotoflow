package com.ff.service;



import com.ff.dto.UserUpdateDto;
import com.ff.entity.User;
import com.ff.vo.UserAddressVO;
import com.ff.vo.UserInfoVO;
import com.ff.vo.UserUpdateVO;

import java.util.List;

public interface UserService {

    // 根据用户id查询用户
    UserInfoVO getUserById(Long userId);

     User getUserByUsername(String username);

     /**
      * 1.修改用户的信息的信息回显
      */
    UserUpdateVO showInfo();

    /**
     * 1.修改用户的信息,修改
     */
    void updateUserInfo(UserUpdateDto userUpdateDto);

}
