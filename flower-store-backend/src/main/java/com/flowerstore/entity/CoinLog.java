package com.flowerstore.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * All In 币流水
 */
@Data
@TableName("t_coin_log")
public class CoinLog implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /** 变动币数（正=增加，负=扣减） */
    private Integer changeCoins;

    private Integer beforeCoins;

    private Integer afterCoins;

    /** 类型：recharge / pay / refund / adjust */
    private String type;

    private String refNo;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
