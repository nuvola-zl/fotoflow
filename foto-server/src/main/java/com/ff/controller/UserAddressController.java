package com.ff.controller;
import com.ff.dto.AddressAddDto;
import com.ff.dto.AddressUpdateDto;
import com.ff.result.Result;
import com.ff.service.UserAddressService;
import com.ff.vo.AddressVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户地址管理控制层
 * 处理地址的新增、查询、修改、删除、设为默认等接口
 */
@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    /**
     * 新增收货地址
     * @param dto 地址新增参数
     * @return 新增后的地址信息
     */
    @PostMapping
    public Result<AddressVO> add(@RequestBody AddressAddDto dto) {
        return Result.success(userAddressService.add(dto));
    }

    /**
     * 查询当前登录用户的所有收货地址
     * @return 地址列表
     */
    @GetMapping("/list")
    public Result<List<AddressVO>> list() {
        return Result.success(userAddressService.list());
    }

    /**
     * 根据地址ID查询单个地址详情
     * @param id 地址ID
     * @return 地址详情
     */
    @GetMapping("/{id}")
    public Result<AddressVO> getById(@PathVariable Long id) {
        return Result.success(userAddressService.getById(id));
    }

    /**
     * 更新收货地址
     * @param id 地址ID
     * @param dto 更新参数
     * @return 更新后的地址信息
     */
    @PutMapping("/{id}")
    public Result<AddressVO> update(@PathVariable Long id, @RequestBody AddressUpdateDto dto) {
        return Result.success(userAddressService.update(id, dto));
    }

    /**
     * 删除收货地址
     * @param id 地址ID
     * @return 无返回值
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userAddressService.delete(id);
        return Result.success();
    }

    /**
     * 将指定地址设为默认地址
     * @param id 地址ID
     * @return 无返回值
     */
    @PutMapping("/{id}/default")
    public Result<Void> setDefault(@PathVariable Long id) {
        userAddressService.setDefault(id);
        return Result.success();
    }
}