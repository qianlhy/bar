package com.flowerstore.controller;

import com.flowerstore.common.Result;
import com.flowerstore.service.CartService;
import com.flowerstore.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 获取购物车列表
     */
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getList(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token);
            List<Map<String, Object>> list = cartService.getListByUserId(userId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 添加到购物车
     */
    @PostMapping
    public Result<String> add(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> params) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token);
            Long productId = Long.valueOf(params.get("productId").toString());
            Integer count = Integer.valueOf(params.get("count").toString());
            cartService.add(userId, productId, count);
            return Result.success("添加成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/{id}")
    public Result<String> updateCount(
            @PathVariable Long id,
            @RequestParam Integer count) {
        try {
            cartService.updateCount(id, count);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        try {
            cartService.delete(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public Result<String> clear(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token);
            cartService.clear(userId);
            return Result.success("清空成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

