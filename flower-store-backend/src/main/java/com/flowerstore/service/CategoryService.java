package com.flowerstore.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flowerstore.entity.Category;
import com.flowerstore.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类服务
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 分页查询分类列表
     */
    public Page<Category> getPage(Integer current, Integer size, String name) {
        Page<Category> page = new Page<>(current, size);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(Category::getName, name);
        }
        wrapper.orderByAsc(Category::getSort);
        return categoryMapper.selectPage(page, wrapper);
    }

    /**
     * 获取所有分类列表
     */
    public List<Category> getList() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getStatus, 1);
        wrapper.orderByAsc(Category::getSort);
        return categoryMapper.selectList(wrapper);
    }

    /**
     * 根据ID查询分类
     */
    public Category getById(Long id) {
        return categoryMapper.selectById(id);
    }

    /**
     * 添加分类
     */
    public void add(Category category) {
        categoryMapper.insert(category);
    }

    /**
     * 更新分类
     */
    public void update(Category category) {
        categoryMapper.updateById(category);
    }

    /**
     * 删除分类
     */
    public void delete(Long id) {
        categoryMapper.deleteById(id);
    }
}

