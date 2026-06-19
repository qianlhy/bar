package com.flowerstore.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flowerstore.entity.Address;
import com.flowerstore.mapper.AddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 地址服务
 */
@Service
public class AddressService {

    @Autowired
    private AddressMapper addressMapper;

    /**
     * 根据用户ID查询地址列表
     */
    public List<Address> getListByUserId(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId);
        wrapper.orderByDesc(Address::getIsDefault);
        wrapper.orderByDesc(Address::getCreateTime);
        return addressMapper.selectList(wrapper);
    }

    /**
     * 根据ID查询地址
     */
    public Address getById(Long id) {
        return addressMapper.selectById(id);
    }

    /**
     * 获取默认地址
     */
    public Address getDefaultAddress(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId);
        wrapper.eq(Address::getIsDefault, 1);
        return addressMapper.selectOne(wrapper);
    }

    /**
     * 添加地址
     */
    @Transactional(rollbackFor = Exception.class)
    public void add(Address address) {
        // 如果设为默认地址，则取消其他默认地址
        if (address.getIsDefault() == 1) {
            cancelDefaultAddress(address.getUserId());
        }
        addressMapper.insert(address);
    }

    /**
     * 更新地址
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Address address) {
        // 如果设为默认地址，则取消其他默认地址
        if (address.getIsDefault() == 1) {
            cancelDefaultAddress(address.getUserId());
        }
        addressMapper.updateById(address);
    }

    /**
     * 删除地址
     */
    public void delete(Long id) {
        addressMapper.deleteById(id);
    }

    /**
     * 取消默认地址
     */
    private void cancelDefaultAddress(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId);
        wrapper.eq(Address::getIsDefault, 1);
        List<Address> addresses = addressMapper.selectList(wrapper);
        for (Address address : addresses) {
            address.setIsDefault(0);
            addressMapper.updateById(address);
        }
    }
}

