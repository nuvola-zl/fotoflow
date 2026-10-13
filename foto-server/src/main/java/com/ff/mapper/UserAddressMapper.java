package com.ff.mapper;
import com.ff.dto.UserAddressDto;
import com.ff.entity.UserAddress;
import com.ff.vo.UserAddressVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserAddressMapper {
    /**
     *查看用户所有地址
     * @param userId
     * @return
     */
    @Select("select * from user_address where user_id = #{userId}")
    List<UserAddressVO> selectMyAddress(Long userId);

    /**
     * 查看我的收货地址详情的回显
     * @param id
     * @return
     */
    @Select("select * from user_address where id = #{id}")
    UserAddress MyAddressShowById(Long id);

    /**
     * 更新我的收货地址
     * @param userAddressDto
     */
    @Update("update user_address set address_detail = #{addressDetail}, phone = #{phone}, receiver = #{receiver}, longitude = #{longitude}, latitude = #{latitude}, is_default = #{isDefault} where id = #{id}")
    void updateMyAddress(UserAddressDto userAddressDto);

    /**
     * 更新我的收货地址的默认地址
     * @param userAddressDto
     */
    @Update("update user_address set is_default = #{isDefault} where user_id = #{userId}")
    void updateMyAddressDefault(UserAddressDto userAddressDto);

    @Insert("insert into user_address (user_id, address_detail, phone, receiver, longitude, latitude, is_default, create_time, update_time) " +
            "values (#{userId}, #{addressDetail}, #{phone}, #{receiver}, #{longitude}, #{latitude}, #{isDefault}, now(), now())")
    void insertMyAddress(UserAddressDto userAddressDto);

    @Delete("delete from user_address where id = #{id} and user_id = #{userId}")
    int deleteMyAddress(@Param("id") Long id, @Param("userId") Long userId);

    @Select("select * from user_address where id = #{id} and user_id = #{userId}")
    UserAddress selectByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Select("select * from user_address where user_id = #{userId} and is_default = 1 limit 1")
    UserAddress selectDefaultByUserId(Long userId);
}
