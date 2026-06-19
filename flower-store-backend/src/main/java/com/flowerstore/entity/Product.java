package com.flowerstore.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体
 */
@Data
@TableName("t_product")
public class Product implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 商品名称 */
    private String name;
    
    /** 分类ID */
    private Long categoryId;
    
    /** 商品价格 */
    private BigDecimal price;
    
    /** 原价 */
    private BigDecimal originalPrice;
    
    /** 库存 */
    private Integer inventory;
    
    /** 销量 */
    private Integer sales;
    
    /** 评分 */
    private BigDecimal rating;
    
    /** 主图 */
    private String image;
    
    /** 轮播图（多张，逗号分隔） */
    private String images;
    
    /** 商品描述 */
    private String description;
    
    /** 商品详情 */
    private String detail;

    /** 规格选项（JSON格式） */
    private String specs;
    
    /** 状态：0-下架，1-上架 */
    private Integer status;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /** 逻辑删除 */
    @TableLogic
    private Integer deleted;
}

