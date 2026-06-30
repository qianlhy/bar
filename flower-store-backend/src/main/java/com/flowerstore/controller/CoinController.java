package com.flowerstore.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flowerstore.common.PageResult;
import com.flowerstore.common.Result;
import com.flowerstore.entity.CoinExchange;
import com.flowerstore.entity.CoinProduct;
import com.flowerstore.service.CoinService;
import com.flowerstore.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * All In币商城控制器
 */
@RestController
@RequestMapping("/coin")
public class CoinController {

    @Autowired
    private CoinService coinService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * All In币商品列表
     */
    @GetMapping("/products")
    public Result<List<CoinProduct>> products() {
        try {
            return Result.success(coinService.listProducts());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 兑换商品
     */
    @PostMapping("/exchange")
    public Result<String> exchange(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> params) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token);
            Long productId = Long.valueOf(params.get("productId").toString());
            coinService.exchange(userId, productId);
            return Result.success("兑换成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 兑换记录
     */
    @GetMapping("/records")
    public Result<List<CoinExchange>> records(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token);
            return Result.success(coinService.listRecords(userId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // ==================== 管理端 ====================

    /**
     * 分页查询商品（管理端，含下架）
     */
    @GetMapping("/admin/products/page")
    public Result<PageResult<CoinProduct>> adminProductPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name) {
        Page<CoinProduct> page = coinService.pageProducts(current, size, name);
        return Result.success(PageResult.of(page));
    }

    /**
     * 新增商品
     */
    @PostMapping("/admin/products")
    public Result<String> adminAddProduct(@RequestBody CoinProduct product) {
        try {
            coinService.saveProduct(product);
            return Result.success("添加成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新商品
     */
    @PutMapping("/admin/products")
    public Result<String> adminUpdateProduct(@RequestBody CoinProduct product) {
        try {
            coinService.updateProduct(product);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/admin/products/{id}")
    public Result<String> adminDeleteProduct(@PathVariable Long id) {
        try {
            coinService.deleteProduct(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询兑换记录（管理端）
     */
    @GetMapping("/admin/exchanges/page")
    public Result<PageResult<Map<String, Object>>> adminExchangePage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId) {
        Page<Map<String, Object>> page = coinService.pageExchanges(current, size, userId);
        return Result.success(PageResult.of(page));
    }
}
