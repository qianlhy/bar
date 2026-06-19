package com.flowerstore.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flowerstore.entity.Favorite;
import com.flowerstore.entity.Product;
import com.flowerstore.mapper.FavoriteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 收藏服务
 */
@Service
public class FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private ProductService productService;

    /**
     * 根据用户ID查询收藏列表
     */
    public List<Product> getListByUserId(Long userId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId);
        wrapper.orderByDesc(Favorite::getCreateTime);
        List<Favorite> favorites = favoriteMapper.selectList(wrapper);

        List<Product> products = new ArrayList<>();
        for (Favorite favorite : favorites) {
            Product product = productService.getById(favorite.getProductId());
            if (product != null) {
                products.add(product);
            }
        }

        return products;
    }

    /**
     * 检查是否已收藏
     */
    public boolean isFavorite(Long userId, Long productId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId);
        wrapper.eq(Favorite::getProductId, productId);
        return favoriteMapper.selectCount(wrapper) > 0;
    }

    /**
     * 添加收藏
     */
    public void add(Long userId, Long productId) {
        // 检查是否已收藏
        if (isFavorite(userId, productId)) {
            return;
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favoriteMapper.insert(favorite);
    }

    /**
     * 取消收藏
     */
    public void remove(Long userId, Long productId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId);
        wrapper.eq(Favorite::getProductId, productId);
        favoriteMapper.delete(wrapper);
    }
}

