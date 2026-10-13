package com.ff.service.impl;

import com.ff.context.BaseContext;
import com.ff.dto.AddressAddDto;
import com.ff.dto.AddressUpdateDto;
import com.ff.entity.UserAddress;
import com.ff.exception.BaseException;
import com.ff.exception.CodeException;
import com.ff.mapper.UserAddressMapper;
import com.ff.service.UserAddressService;
import com.ff.vo.AddressVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户地址业务实现类
 * 处理地址的新增、查询、修改、删除、默认设置等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressMapper userAddressMapper;

    /**
     * 新增收货地址
     * 第一个地址自动设为默认，若指定默认则取消其他默认
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AddressVO add(AddressAddDto dto) {
        Long userId = BaseContext.getCurrentId();

        // 1. 构建实体
        UserAddress address = new UserAddress();
        BeanUtils.copyProperties(dto, address);
        address.setUserId(userId);
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());

        // 2. 默认地址处理
        // 如果用户没有地址，第一个自动设为默认
        int count = userAddressMapper.countByUserId(userId);
        if (count == 0) {
            address.setIsDefault(1);
        } else {
            // 如果前端传了 isDefault=1，需要取消其他默认
            if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
                userAddressMapper.cancelDefaultByUserId(userId);
                address.setIsDefault(1);
            } else {
                address.setIsDefault(0);
            }
        }

        userAddressMapper.insert(address);
        return toVO(address);
    }

    /**
     * 查询当前用户的所有收货地址
     * 默认地址排在最前面
     */
    @Override
    public List<AddressVO> list() {
        Long userId = BaseContext.getCurrentId();
        List<UserAddress> list = userAddressMapper.selectByUserId(userId);
        // 默认地址排在最前面
        return list.stream()
                .sorted((a, b) -> b.getIsDefault().compareTo(a.getIsDefault()))
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID查询单个地址
     * 校验数据归属权，防止越权访问
     */
    @Override
    public AddressVO getById(Long id) {
        Long userId = BaseContext.getCurrentId();
        UserAddress address = userAddressMapper.selectById(id);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BaseException(CodeException.NOT_FOUND, "地址不存在");
        }
        return toVO(address);
    }

    /**
     * 更新收货地址
     * 仅允许修改自己的地址
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AddressVO update(Long id, AddressUpdateDto dto) {
        Long userId = BaseContext.getCurrentId();

        UserAddress address = userAddressMapper.selectById(id);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BaseException(CodeException.NOT_FOUND, "地址不存在");
        }

        BeanUtils.copyProperties(dto, address);
        address.setId(id);
        address.setUpdateTime(LocalDateTime.now());
        userAddressMapper.update(address);
        return toVO(address);
    }

    /**
     * 删除收货地址
     * 如果删除的是默认地址，则自动将最早创建的地址设为默认
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Long userId = BaseContext.getCurrentId();

        UserAddress address = userAddressMapper.selectById(id);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BaseException(CodeException.NOT_FOUND, "地址不存在");
        }

        userAddressMapper.deleteById(id);

        // 如果删的是默认地址，把最早创建的一个设为默认
        if (address.getIsDefault() == 1) {
            UserAddress first = userAddressMapper.selectFirstByUserId(userId);
            if (first != null) {
                userAddressMapper.updateDefault(first.getId(), 1);
            }
        }
    }

    /**
     * 设置默认地址
     * 先取消该用户所有默认地址，再将当前地址设为默认
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Long id) {
        Long userId = BaseContext.getCurrentId();

        UserAddress address = userAddressMapper.selectById(id);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BaseException(CodeException.NOT_FOUND, "地址不存在");
        }

        // 1. 取消该用户所有默认
        userAddressMapper.cancelDefaultByUserId(userId);

        // 2. 设置当前为默认
        userAddressMapper.updateDefault(id, 1);
    }

    /**
     * 将UserAddress实体转换为AddressVO
     */
    private AddressVO toVO(UserAddress address) {
        return AddressVO.builder()
                .id(address.getId())
                .addressDetail(address.getAddressDetail())
                .phone(address.getPhone())
                .receiver(address.getReceiver())
                .longitude(address.getLongitude())
                .latitude(address.getLatitude())
                .tag(address.getTag())
                .isDefault(address.getIsDefault())
                .createTime(address.getCreateTime())
                .build();
    }
}