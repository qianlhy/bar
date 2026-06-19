package com.flowerstore.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flowerstore.entity.CoinExchange;
import com.flowerstore.entity.CoinProduct;
import com.flowerstore.entity.User;
import com.flowerstore.mapper.CoinExchangeMapper;
import com.flowerstore.mapper.CoinProductMapper;
import com.flowerstore.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 27币商城服务
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
     * 查询上架的27币商品列表
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
            throw new RuntimeException("27币不足");
        }

        // 扣减27币
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
}
