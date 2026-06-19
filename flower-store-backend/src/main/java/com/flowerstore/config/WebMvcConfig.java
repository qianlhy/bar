package com.flowerstore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.static-pattern}")
    private String staticPattern;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置文件上传的静态资源访问
        registry.addResourceHandler("/api/uploads/**")
                .addResourceLocations("file:" + uploadPath);
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
    }
}

