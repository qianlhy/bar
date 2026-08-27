package com.flowerstore.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flowerstore.entity.OperationLock;
import com.flowerstore.mapper.OperationLockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作锁：防止多名员工同时录入同一用户积分 / 处理同一订单
 */
@Service
public class OperationLockService {

    /** 默认锁持有时间（分钟） */
    private static final int LOCK_MINUTES = 5;

    @Autowired
    private OperationLockMapper lockMapper;

    /**
     * 尝试获取锁。成功返回成功信息；失败抛出异常说明被谁占用。
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> tryLock(String lockKey, Long adminId, String adminName) {
        cleanupExpired(lockKey);
        OperationLock exist = findByKey(lockKey);
        if (exist != null) {
            if (exist.getAdminId().equals(adminId)) {
                // 自己续期
                exist.setExpireTime(LocalDateTime.now().plusMinutes(LOCK_MINUTES));
                exist.setAdminName(adminName);
                lockMapper.updateById(exist);
                return toMap(exist, true);
            }
            throw new RuntimeException("操作冲突：" + exist.getAdminName() + " 正在处理，请稍后再试");
        }

        OperationLock lock = new OperationLock();
        lock.setLockKey(lockKey);
        lock.setAdminId(adminId);
        lock.setAdminName(adminName);
        lock.setExpireTime(LocalDateTime.now().plusMinutes(LOCK_MINUTES));
        try {
            lockMapper.insert(lock);
        } catch (Exception e) {
            OperationLock again = findByKey(lockKey);
            if (again != null && !again.getAdminId().equals(adminId)) {
                throw new RuntimeException("操作冲突：" + again.getAdminName() + " 正在处理，请稍后再试");
            }
            throw new RuntimeException("获取操作锁失败，请重试");
        }
        return toMap(lock, true);
    }

    /**
     * 查询锁状态（不占用）
     */
    public Map<String, Object> status(String lockKey, Long adminId) {
        cleanupExpired(lockKey);
        OperationLock exist = findByKey(lockKey);
        if (exist == null) {
            Map<String, Object> map = new HashMap<>();
            map.put("locked", false);
            map.put("mine", false);
            return map;
        }
        return toMap(exist, exist.getAdminId().equals(adminId));
    }

    /**
     * 释放锁（仅持有者可释放）
     */
    @Transactional(rollbackFor = Exception.class)
    public void unlock(String lockKey, Long adminId) {
        OperationLock exist = findByKey(lockKey);
        if (exist == null) {
            return;
        }
        if (!exist.getAdminId().equals(adminId)) {
            throw new RuntimeException("无权释放他人的操作锁");
        }
        lockMapper.deleteById(exist.getId());
    }

    private OperationLock findByKey(String lockKey) {
        LambdaQueryWrapper<OperationLock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OperationLock::getLockKey, lockKey);
        return lockMapper.selectOne(wrapper);
    }

    private void cleanupExpired(String lockKey) {
        LambdaQueryWrapper<OperationLock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OperationLock::getLockKey, lockKey);
        wrapper.lt(OperationLock::getExpireTime, LocalDateTime.now());
        lockMapper.delete(wrapper);
    }

    private Map<String, Object> toMap(OperationLock lock, boolean mine) {
        Map<String, Object> map = new HashMap<>();
        map.put("locked", true);
        map.put("mine", mine);
        map.put("adminId", lock.getAdminId());
        map.put("adminName", lock.getAdminName());
        map.put("expireTime", lock.getExpireTime());
        return map;
    }
}
