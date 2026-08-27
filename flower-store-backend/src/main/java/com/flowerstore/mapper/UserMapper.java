package com.flowerstore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flowerstore.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 乐观扣币：余额不足时影响行数为 0
     */
    @Update("UPDATE t_user SET coins = coins - #{amount}, update_time = NOW() " +
            "WHERE id = #{userId} AND deleted = 0 AND coins >= #{amount}")
    int deductCoins(@Param("userId") Long userId, @Param("amount") int amount);

    /**
     * 加币
     */
    @Update("UPDATE t_user SET coins = IFNULL(coins,0) + #{amount}, update_time = NOW() " +
            "WHERE id = #{userId} AND deleted = 0")
    int addCoins(@Param("userId") Long userId, @Param("amount") int amount);
}
