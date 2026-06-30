package com.flowerstore.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员充值套餐实体
 */
@Data
@TableName("t_recharge_package")
public class RechargePackage implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 套餐名称 */
    private String name;

    /** 实付金额 */
    private BigDecimal payAmount;

    /** 到账余额 */
    private BigDecimal balance;

    /** 赠送All In币 */
    private Integer giftCoins;

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
