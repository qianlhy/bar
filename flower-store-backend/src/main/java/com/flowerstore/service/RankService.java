package com.flowerstore.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flowerstore.entity.User;
import com.flowerstore.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 排行榜服务
 */
@Service
public class RankService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 按大师分查询排行榜（取前50名）
     *
     * @param type 榜单类型：month/lastMonth/quarter/lastQuarter
     *             当前按大师分总分排序，预留类型参数用于后续按周期统计
     */
    public List<User> getRankList(String type) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getStatus, 1);
        wrapper.gt(User::getMasterScore, 0);
        wrapper.orderByDesc(User::getMasterScore);
        wrapper.last("LIMIT 50");
        List<User> users = userMapper.selectList(wrapper);
        // 隐藏敏感信息
        for (User user : users) {
            user.setPassword(null);
            user.setOpenid(null);
            user.setPhone(null);
        }
        return users;
    }
}
