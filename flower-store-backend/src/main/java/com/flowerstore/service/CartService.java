package com.flowerstore.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flowerstore.entity.Cart;
import com.flowerstore.entity.Product;
import com.flowerstore.mapper.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车服务
 */
@Service
public class CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductService productService;

    /**
     * 根据用户ID查询购物车列表
     */
    public List<Map<String, Object>> getListByUserId(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        List<Cart> carts = cartMapper.selectList(wrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Cart cart : carts) {
            Product product = productService.getById(cart.getProductId());
            if (product != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", cart.getId());
                item.put("productId", product.getId());
                item.put("name", product.getName());
                item.put("price", product.getPrice());
                item.put("image", product.getImage());
                item.put("inventory", product.getInventory());
                item.put("count", cart.getCount());
                result.add(item);
            }
        }

        return result;
    }

    /**
     * 添加到购物车
     */
    public void add(Long userId, Long productId, Integer count) {
        // 检查商品是否已在购物车中
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        wrapper.eq(Cart::getProductId, productId);
        Cart cart = cartMapper.selectOne(wrapper);

        if (cart != null) {
            // 如果已存在，则增加数量
            cart.setCount(cart.getCount() + count);
            cartMapper.updateById(cart);
        } else {
            // 如果不存在，则新增
            cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setCount(count);
            cartMapper.insert(cart);
        }
    }

    /**
     * 更新购物车商品数量
     */
    public void updateCount(Long id, Integer count) {
        Cart cart = cartMapper.selectById(id);
        if (cart != null) {
            if (count <= 0) {
                // 如果数量小于等于0，则删除
                cartMapper.deleteById(id);
            } else {
                cart.setCount(count);
                cartMapper.updateById(cart);
            }
        }
    }

    /**
     * 删除购物车商品
     */
    public void delete(Long id) {
        cartMapper.deleteById(id);
    }

    /**
     * 清空购物车
     */
    public void clear(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        cartMapper.delete(wrapper);
    }
}

