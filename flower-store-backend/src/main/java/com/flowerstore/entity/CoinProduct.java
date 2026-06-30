package com.flowerstore.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * All In币商品实体
 */
@Data
@TableName("t_coin_product")
public class CoinProduct implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商品名称 */
    private String name;

    /** 商品图片 */
    private String image;

    /** 兑换所需All In币 */
    private Integer coinPrice;

    /** 库存 */
    private Integer stock;

    /** 排序 */
    private Integer sort;

    /** 状态：0-下架，1-上架 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
