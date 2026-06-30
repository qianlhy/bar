package com.flowerstore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flowerstore.entity.CoinExchange;
import org.apache.ibatis.annotations.Mapper;

/**
 * All In币兑换记录Mapper
 */
@Mapper
public interface CoinExchangeMapper extends BaseMapper<CoinExchange> {
}
