package com.flowerstore.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员充值订单实体
 */
@Data
@TableName("t_recharge_order")
public class RechargeOrder implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单号 */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /** 套餐ID */
    private Long packageId;

    /** 实付金额 */
    private BigDecimal payAmount;

    /** 到账余额 */
    private BigDecimal balance;

    /** 赠送All In币 */
    private Integer giftCoins;

    /** 支付方式：wechat-微信支付 */
    private String payMethod;

    /** 状态：0-待支付，1-已到账，2-已取消 */
    private Integer status;

    /** 备注 */
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
