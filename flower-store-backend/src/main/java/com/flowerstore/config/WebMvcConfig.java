package com.flowerstore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private FileUploadProperties uploadProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = "file:" + uploadProperties.getResolvedPath();
        // 上下文路径已是 /api，所以 /uploads/** 对外即 /api/uploads/**
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
        // 兼容历史数据里带双重 /api 前缀的图片地址
        registry.addResourceHandler("/api/uploads/**")
                .addResourceLocations(location);
    }
}
