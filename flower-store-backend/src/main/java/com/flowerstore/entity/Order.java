package com.flowerstore.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@TableName("t_order")
public class Order implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 订单号 */
    private String orderNo;
    
    /** 用户ID */
    private Long userId;
    
    /** 收货人姓名 */
    private String receiverName;
    
    /** 收货人手机号 */
    private String receiverPhone;
    
    /** 收货地址-省 */
    private String province;
    
    /** 收货地址-市 */
    private String city;
    
    /** 收货地址-区 */
    private String district;
    
    /** 详细地址 */
    private String address;
    
    /** 商品总价 */
    private BigDecimal totalPrice;
    
    /** 运费 */
    private BigDecimal freight;
    
    /** 实付金额 */
    private BigDecimal actualPayment;
    
    /** 支付方式：online-在线支付，delivery-货到付款 */
    private String paymentMethod;
    
    /** 订单备注 */
    private String remark;
    
    /** 订单状态：1-待付款，2-待发货，3-已发货，4-已完成，5-已取消 */
    private Integer status;
    
    /** 支付时间 */
    private LocalDateTime payTime;
    
    /** 发货时间 */
    private LocalDateTime deliveryTime;
    
    /** 完成时间 */
    private LocalDateTime finishTime;
    
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

