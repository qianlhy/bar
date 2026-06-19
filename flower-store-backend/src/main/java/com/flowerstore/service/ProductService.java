package com.flowerstore.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flowerstore.entity.Product;
import com.flowerstore.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品服务
 */
@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    /**
     * 分页查询商品列表
     */
    public Page<Product> getPage(Integer current, Integer size, String name, Long categoryId, Integer status) {
        Page<Product> page = new Page<>(current, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.isEmpty()) {
            wrapper.like(Product::getName, name);
        }
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }
        
        wrapper.orderByDesc(Product::getCreateTime);
        return productMapper.selectPage(page, wrapper);
    }

    /**
     * 获取所有商品列表（小程序端）
     */
    public List<Product> getList(Long categoryId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(Product::getCreateTime);
        return productMapper.selectList(wrapper);
    }

    /**
     * 根据ID查询商品
     */
    public Product getById(Long id) {
        return productMapper.selectById(id);
    }

    /**
     * 添加商品
     */
    public void add(Product product) {
        productMapper.insert(product);
    }

    /**
     * 更新商品
     */
    public void update(Product product) {
        productMapper.updateById(product);
    }

    /**
     * 删除商品
     */
    public void delete(Long id) {
        productMapper.deleteById(id);
    }

    /**
     * 更新商品库存
     */
    public void updateInventory(Long id, Integer quantity) {
        Product product = productMapper.selectById(id);
        if (product != null) {
            product.setInventory(product.getInventory() - quantity);
            product.setSales(product.getSales() + quantity);
            productMapper.updateById(product);
        }
    }

    /**
     * 获取热门商品（按销量排序）
     */
    public List<Product> getHotProducts(Integer limit) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);
        wrapper.orderByDesc(Product::getSales);
        wrapper.last("LIMIT " + limit);
        return productMapper.selectList(wrapper);
    }

    /**
     * 获取新品商品（按创建时间排序）
     */
    public List<Product> getNewProducts(Integer limit) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);
        wrapper.orderByDesc(Product::getCreateTime);
        wrapper.last("LIMIT " + limit);
        return productMapper.selectList(wrapper);
    }

    /**
     * 搜索商品
     */
    public List<Product> searchProducts(String keyword) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);
        wrapper.like(Product::getName, keyword);
        return productMapper.selectList(wrapper);
    }
}

