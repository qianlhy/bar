package com.flowerstore.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 27币兑换记录实体
 */
@Data
@TableName("t_coin_exchange")
public class CoinExchange implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 27币商品ID */
    private Long coinProductId;

    /** 商品名称（快照） */
    private String productName;

    /** 消耗27币 */
    private Integer coinPrice;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
