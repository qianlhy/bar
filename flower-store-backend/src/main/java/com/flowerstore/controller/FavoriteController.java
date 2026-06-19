package com.flowerstore.controller;

import com.flowerstore.common.Result;
import com.flowerstore.entity.Product;
import com.flowerstore.service.FavoriteService;
import com.flowerstore.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 收藏控制器
 */
@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 获取收藏列表
     */
    @GetMapping("/list")
    public Result<List<Product>> getList(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token);
            List<Product> list = favoriteService.getListByUserId(userId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/check")
    public Result<Boolean> check(
            @RequestHeader("Authorization") String token,
            @RequestParam Long productId) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token);
            boolean isFavorite = favoriteService.isFavorite(userId, productId);
            return Result.success(isFavorite);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 添加收藏
     */
    @PostMapping
    public Result<String> add(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> params) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token);
            Long productId = Long.valueOf(params.get("productId").toString());
            favoriteService.add(userId, productId);
            return Result.success("收藏成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消收藏
     */
    @DeleteMapping
    public Result<String> remove(
            @RequestHeader("Authorization") String token,
            @RequestParam Long productId) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token);
            favoriteService.remove(userId, productId);
            return Result.success("取消收藏成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

