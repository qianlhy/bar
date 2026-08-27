package com.flowerstore.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("t_user")
public class User implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 微信OpenID */
    private String openid;
    
    /** 昵称 */
    private String nickname;
    
    /** 头像 */
    private String avatar;
    
    /** 手机号 */
    private String phone;
    
    /** 密码（MD5加密） */
    private String password;
    
    /** 性别：0-未知，1-男，2-女 */
    private Integer gender;
    
    /** 用户类型：1-普通用户，2-管理员 */
    private Integer userType;
    
    /** 状态：0-禁用，1-正常 */
    private Integer status;

    /** 账户余额 */
    private java.math.BigDecimal balance;

    /** All In币 */
    private Integer coins;

    /** 大师分 */
    private Integer masterScore;

    /** 优惠券数量 */
    private Integer couponCount;

    /** 可用积分（每日0点清零） */
    private Integer points;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /** 逻辑删除：0-未删除，1-已删除 */
    @TableLogic
    private Integer deleted;
}

