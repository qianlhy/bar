package com.flowerstore.controller;

import com.flowerstore.common.Result;
import com.flowerstore.entity.Address;
import com.flowerstore.service.AddressService;
import com.flowerstore.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址控制器
 */
@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 获取地址列表
     */
    @GetMapping("/list")
    public Result<List<Address>> getList(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token);
            List<Address> list = addressService.getListByUserId(userId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据ID查询地址
     */
    @GetMapping("/{id}")
    public Result<Address> getById(@PathVariable Long id) {
        Address address = addressService.getById(id);
        return Result.success(address);
    }

    /**
     * 获取默认地址
     */
    @GetMapping("/default")
    public Result<Address> getDefaultAddress(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token);
            Address address = addressService.getDefaultAddress(userId);
            return Result.success(address);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 添加地址
     */
    @PostMapping
    public Result<String> add(
            @RequestHeader("Authorization") String token,
            @RequestBody Address address) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token);
            address.setUserId(userId);
            addressService.add(address);
            return Result.success("添加成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新地址
     */
    @PutMapping
    public Result<String> update(@RequestBody Address address) {
        try {
            addressService.update(address);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除地址
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        try {
            addressService.delete(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

