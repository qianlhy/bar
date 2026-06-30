package com.flowerstore.controller;

import com.flowerstore.common.Result;
import com.flowerstore.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 系统配置控制器（门店信息 / WiFi 等）
 */
@RestController
@RequestMapping("/config")
public class SystemConfigController {

    @Autowired
    private SystemConfigService configService;

    /**
     * 公开配置（小程序端读取门店信息、WiFi 等）
     */
    @GetMapping("/public")
    public Result<Map<String, String>> publicConfig() {
        return Result.success(configService.getAll());
    }

    /**
     * 全部配置（管理端）
     */
    @GetMapping("/all")
    public Result<Map<String, String>> all() {
        return Result.success(configService.getAll());
    }

    /**
     * 保存配置（管理端）
     */
    @PostMapping("/save")
    public Result<String> save(@RequestBody Map<String, Object> params) {
        try {
            configService.save(params);
            return Result.success("保存成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
