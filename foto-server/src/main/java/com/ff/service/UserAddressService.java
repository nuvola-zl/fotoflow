package com.ff.service;

import com.ff.dto.AddressAddDto;
import com.ff.dto.AddressUpdateDto;
import com.ff.vo.AddressVO;

import java.util.List;

/**
 * 用户地址业务接口
 * 定义地址相关业务方法
 */
public interface UserAddressService {

    /**
     * 新增收货地址
     * @param dto 新增地址参数
     * @return 地址VO
     */
    AddressVO add(AddressAddDto dto);

    /**
     * 查询当前用户地址列表
     * @return 地址VO列表
     */
    List<AddressVO> list();

    /**
     * 根据ID查询地址
     * @param id 地址ID
     * @return 地址VO
     */
    AddressVO getById(Long id);

    /**
     * 删除地址
     * @param id 地址ID
     */
    void delete(Long id);

    /**
     * 设置默认地址
     * @param id 地址ID
     */
    void setDefault(Long id);

    /**
     * 更新地址
     * @param id 地址ID
     * @param dto 更新参数
     * @return 地址VO
     */
    AddressVO update(Long id, AddressUpdateDto dto);
}