package com.flowerstore;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 梭哈酒馆应用启动类
 */
@SpringBootApplication
@MapperScan("com.flowerstore.mapper")
public class FlowerStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowerStoreApplication.class, args);
        System.out.println("梭哈酒馆后端系统启动成功！");
    }
}

