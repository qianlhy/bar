package com.flowerstore.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flowerstore.entity.SystemConfig;
import com.flowerstore.mapper.SystemConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统配置服务
 */
@Service
public class SystemConfigService {

    @Autowired
    private SystemConfigMapper configMapper;

    /**
     * 获取全部配置（key -> value）
     */
    public Map<String, String> getAll() {
        List<SystemConfig> list = configMapper.selectList(null);
        Map<String, String> map = new LinkedHashMap<>();
        for (SystemConfig c : list) {
            map.put(c.getConfigKey(), c.getConfigValue());
        }
        return map;
    }

    /**
     * 批量保存配置（存在则更新，不存在则新增）
     */
    public void save(Map<String, Object> params) {
        if (params == null) {
            return;
        }
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue() == null ? "" : entry.getValue().toString();
            LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SystemConfig::getConfigKey, key);
            SystemConfig exist = configMapper.selectOne(wrapper);
            if (exist == null) {
                SystemConfig config = new SystemConfig();
                config.setConfigKey(key);
                config.setConfigValue(value);
                configMapper.insert(config);
            } else {
                exist.setConfigValue(value);
                configMapper.updateById(exist);
            }
        }
    }
}
