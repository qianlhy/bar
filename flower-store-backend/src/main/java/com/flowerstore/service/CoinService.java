package com.flowerstore.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flowerstore.entity.CoinExchange;
import com.flowerstore.entity.CoinProduct;
import com.flowerstore.entity.User;
import com.flowerstore.mapper.CoinExchangeMapper;
import com.flowerstore.mapper.CoinProductMapper;
import com.flowerstore.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * All In币商城服务
 */
@Service
public class CoinService {

    @Autowired
    private CoinProductMapper coinProductMapper;

    @Autowired
    private CoinExchangeMapper coinExchangeMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 查询上架的All In币商品列表
     */
    public List<CoinProduct> listProducts() {
        LambdaQueryWrapper<CoinProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CoinProduct::getStatus, 1);
        wrapper.orderByAsc(CoinProduct::getSort);
        return coinProductMapper.selectList(wrapper);
    }

    /**
     * 兑换商品
     */
    @Transactional(rollbackFor = Exception.class)
    public void exchange(Long userId, Long productId) {
        CoinProduct product = coinProductMapper.selectById(productId);
        if (product == null || product.getStatus() == null || product.getStatus() != 1) {
            throw new RuntimeException("商品不存在或已下架");
        }
        if (product.getStock() == null || product.getStock() <= 0) {
            throw new RuntimeException("库存不足");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        int userCoins = user.getCoins() == null ? 0 : user.getCoins();
        if (userCoins < product.getCoinPrice()) {
            throw new RuntimeException("All In币不足");
        }

        // 扣减All In币
        user.setCoins(userCoins - product.getCoinPrice());
        userMapper.updateById(user);

        // 扣减库存
        product.setStock(product.getStock() - 1);
        coinProductMapper.updateById(product);

        // 记录兑换流水
        CoinExchange exchange = new CoinExchange();
        exchange.setUserId(userId);
        exchange.setCoinProductId(productId);
        exchange.setProductName(product.getName());
        exchange.setCoinPrice(product.getCoinPrice());
        coinExchangeMapper.insert(exchange);
    }

    /**
     * 查询用户兑换记录
     */
    public List<CoinExchange> listRecords(Long userId) {
        LambdaQueryWrapper<CoinExchange> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CoinExchange::getUserId, userId);
        wrapper.orderByDesc(CoinExchange::getCreateTime);
        return coinExchangeMapper.selectList(wrapper);
    }

    // ==================== 管理端 ====================

    /**
     * 分页查询商品（含下架）
     */
    public Page<CoinProduct> pageProducts(Integer current, Integer size, String name) {
        Page<CoinProduct> page = new Page<>(current, size);
        LambdaQueryWrapper<CoinProduct> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(CoinProduct::getName, name);
        }
        wrapper.orderByAsc(CoinProduct::getSort).orderByDesc(CoinProduct::getId);
        return coinProductMapper.selectPage(page, wrapper);
    }

    public void saveProduct(CoinProduct product) {
        coinProductMapper.insert(product);
    }

    public void updateProduct(CoinProduct product) {
        coinProductMapper.updateById(product);
    }

    public void deleteProduct(Long id) {
        coinProductMapper.deleteById(id);
    }

    /**
     * 分页查询兑换记录（管理端，附带用户昵称）
     */
    public Page<Map<String, Object>> pageExchanges(Integer current, Integer size, Long userId) {
        Page<CoinExchange> page = new Page<>(current, size);
        LambdaQueryWrapper<CoinExchange> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(CoinExchange::getUserId, userId);
        }
        wrapper.orderByDesc(CoinExchange::getCreateTime);
        Page<CoinExchange> result = coinExchangeMapper.selectPage(page, wrapper);

        Map<Long, String> nicknameMap = new HashMap<>();
        List<Long> userIds = result.getRecords().stream()
                .map(CoinExchange::getUserId).distinct().collect(Collectors.toList());
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            for (User u : users) {
                nicknameMap.put(u.getId(), u.getNickname());
            }
        }

        List<Map<String, Object>> records = result.getRecords().stream().map(e -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", e.getId());
            m.put("userId", e.getUserId());
            m.put("nickname", nicknameMap.get(e.getUserId()));
            m.put("coinProductId", e.getCoinProductId());
            m.put("productName", e.getProductName());
            m.put("coinPrice", e.getCoinPrice());
            m.put("createTime", e.getCreateTime());
            return m;
        }).collect(Collectors.toList());

        Page<Map<String, Object>> mapPage = new Page<>(current, size, result.getTotal());
        mapPage.setRecords(records);
        return mapPage;
    }
}
