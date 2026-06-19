package com.flowerstore.controller;

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
 * 27币商城控制器
 */
@RestController
@RequestMapping("/coin")
public class CoinController {

    @Autowired
    private CoinService coinService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 27币商品列表
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
}
