package com.flowerstore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private FileUploadProperties uploadProperties;

    @Autowired
    private AdminRoleInterceptor adminRoleInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = "file:" + uploadProperties.getResolvedPath();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
        registry.addResourceHandler("/api/uploads/**")
                .addResourceLocations(location);
        registry.addResourceHandler("/fonts/**")
                .addResourceLocations("classpath:/static/fonts/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminRoleInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/uploads/**", "/api/uploads/**", "/fonts/**");
    }
}
