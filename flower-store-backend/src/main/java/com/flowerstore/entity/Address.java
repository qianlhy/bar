package com.flowerstore.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收货地址实体
 */
@Data
@TableName("t_address")
public class Address implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 收货人姓名 */
    private String name;
    
    /** 收货人手机号 */
    private String phone;
    
    /** 省 */
    private String province;
    
    /** 市 */
    private String city;
    
    /** 区 */
    private String district;
    
    /** 详细地址 */
    private String address;
    
    /** 是否默认地址：0-否，1-是 */
    private Integer isDefault;
    
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

