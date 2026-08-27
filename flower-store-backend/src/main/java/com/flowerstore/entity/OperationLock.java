package com.flowerstore.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作锁（员工录入积分 / 查看订单防冲突）
 */
@Data
@TableName("t_operation_lock")
public class OperationLock implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String lockKey;

    private Long adminId;

    private String adminName;

    private LocalDateTime expireTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
