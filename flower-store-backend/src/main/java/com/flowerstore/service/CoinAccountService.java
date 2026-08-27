package com.flowerstore.service;

import com.flowerstore.entity.CoinLog;
import com.flowerstore.entity.User;
import com.flowerstore.mapper.CoinLogMapper;
import com.flowerstore.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * All In 币账户：充值到账 / 点单扣币（与币商城 CoinService 分离）
 */
@Service
public class CoinAccountService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CoinLogMapper coinLogMapper;

    public int getCoins(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getCoins() == null) {
            return 0;
        }
        return user.getCoins();
    }

    @Transactional(rollbackFor = Exception.class)
    public void credit(Long userId, int amount, String type, String refNo, String remark) {
        if (amount <= 0) {
            return;
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        int before = user.getCoins() == null ? 0 : user.getCoins();
        int rows = userMapper.addCoins(userId, amount);
        if (rows == 0) {
            throw new RuntimeException("加币失败");
        }
        writeLog(userId, amount, before, before + amount, type, refNo, remark);
    }

    /**
     * 扣币（乐观条件更新，并发安全）
     */
    @Transactional(rollbackFor = Exception.class)
    public void deduct(Long userId, int amount, String type, String refNo, String remark) {
        if (amount <= 0) {
            return;
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        int before = user.getCoins() == null ? 0 : user.getCoins();
        if (before < amount) {
            throw new RuntimeException("All In 币不足");
        }
        int rows = userMapper.deductCoins(userId, amount);
        if (rows == 0) {
            throw new RuntimeException("All In 币不足");
        }
        writeLog(userId, -amount, before, before - amount, type, refNo, remark);
    }

    private void writeLog(Long userId, int change, int before, int after,
                          String type, String refNo, String remark) {
        CoinLog log = new CoinLog();
        log.setUserId(userId);
        log.setChangeCoins(change);
        log.setBeforeCoins(before);
        log.setAfterCoins(after);
        log.setType(type);
        log.setRefNo(refNo);
        log.setRemark(remark);
        coinLogMapper.insert(log);
    }
}
