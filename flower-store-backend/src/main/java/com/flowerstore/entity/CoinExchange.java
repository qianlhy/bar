package com.flowerstore.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * All In币兑换记录实体
 */
@Data
@TableName("t_coin_exchange")
public class CoinExchange implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** All In币商品ID */
    private Long coinProductId;

    /** 商品名称（快照） */
    private String productName;

    /** 消耗All In币 */
    private Integer coinPrice;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
