package com.flowerstore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flowerstore.entity.RechargeOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 充值订单Mapper
 */
@Mapper
public interface RechargeOrderMapper extends BaseMapper<RechargeOrder> {
}
