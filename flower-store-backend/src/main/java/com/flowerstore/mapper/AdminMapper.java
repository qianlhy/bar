package com.flowerstore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flowerstore.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员Mapper
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
}

