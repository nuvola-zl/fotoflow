package com.ff.mapper;

import com.ff.entity.UserAddress;
import com.ff.vo.UserAddressVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 用户地址数据访问层
 * 负责地址表的CRUD操作
 */
@Mapper
public interface UserAddressMapper {

    /**
     * 根据ID查询地址
     * @param id 地址ID
     * @return 地址实体
     */
    UserAddress selectById(Long id);

    /**
     * 新增地址
     * @param address 地址实体
     * @return 影响行数
     */
    int insert(UserAddress address);

    /**
     * 更新地址
     * @param address 地址实体
     * @return 影响行数
     */
    int update(UserAddress address);

    /**
     * 根据ID删除地址
     * @param id 地址ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 根据用户ID查询所有地址
     * @param userId 用户ID
     * @return 地址列表
     */
    List<UserAddress> selectByUserId(Long userId);

    /**
     * 查询用户默认地址
     * @param userId 用户ID
     * @return 默认地址
     */
    UserAddress selectDefaultByUserId(Long userId);

    /**
     * 查询用户最早创建的一条地址
     * @param userId 用户ID
     * @return 最早地址
     */
    UserAddress selectFirstByUserId(Long userId);

    /**
     * 统计用户地址数量
     * @param userId 用户ID
     * @return 地址总数
     */
    int countByUserId(Long userId);

    /**
     * 取消该用户的所有默认地址
     * @param userId 用户ID
     * @return 影响行数
     */
    int cancelDefaultByUserId(Long userId);

    /**
     * 更新地址的默认状态
     * @param id 地址ID
     * @param isDefault 是否默认
     * @return 影响行数
     */
    int updateDefault(@Param("id") Long id, @Param("isDefault") Integer isDefault);
}