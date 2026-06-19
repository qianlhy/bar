package com.flowerstore.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flowerstore.common.PageResult;
import com.flowerstore.common.Result;
import com.flowerstore.entity.Product;
import com.flowerstore.mapper.ProductMapper;
import com.flowerstore.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 分页查询商品列表（管理端）
     */
    @GetMapping("/page")
    public Result<PageResult<Product>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        Page<Product> page = productService.getPage(current, size, name, categoryId, status);
        return Result.success(PageResult.of(page));
    }

    /**
     * 获取所有商品列表（小程序端）
     */
    @GetMapping("/list")
    public Result<List<Product>> getList() {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1); // 只返回上架的商品
        wrapper.orderByDesc(Product::getCreateTime);
        List<Product> list = productMapper.selectList(wrapper);
        return Result.success(list);
    }

    /**
     * 根据分类ID获取商品列表
     */
    @GetMapping("/category/{categoryId}")
    public Result<List<Product>> getListByCategory(@PathVariable Long categoryId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getCategoryId, categoryId);
        wrapper.eq(Product::getStatus, 1); // 只返回上架的商品
        wrapper.orderByDesc(Product::getCreateTime);
        List<Product> list = productMapper.selectList(wrapper);
        return Result.success(list);
    }

    /**
     * 根据ID查询商品详情
     */
    @GetMapping("/{id}")
    public Result<Product> getById(@PathVariable Long id) {
        Product product = productService.getById(id);
        return Result.success(product);
    }

    /**
     * 添加商品
     */
    @PostMapping
    public Result<String> add(@RequestBody Product product) {
        try {
            productService.add(product);
            return Result.success("添加成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新商品
     */
    @PutMapping
    public Result<String> update(@RequestBody Product product) {
        try {
            productService.update(product);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        try {
            productService.delete(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取热门商品
     */
    @GetMapping("/hot")
    public Result<List<Product>> getHotProducts(@RequestParam(defaultValue = "6") Integer limit) {
        List<Product> list = productService.getHotProducts(limit);
        return Result.success(list);
    }

    /**
     * 获取新品商品
     */
    @GetMapping("/new")
    public Result<List<Product>> getNewProducts(@RequestParam(defaultValue = "6") Integer limit) {
        List<Product> list = productService.getNewProducts(limit);
        return Result.success(list);
    }

    /**
     * 搜索商品
     */
    @GetMapping("/search")
    public Result<List<Product>> searchProducts(@RequestParam String keyword) {
        List<Product> list = productService.searchProducts(keyword);
        return Result.success(list);
    }
}
