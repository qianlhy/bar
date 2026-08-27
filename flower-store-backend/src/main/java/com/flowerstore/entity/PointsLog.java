package com.flowerstore.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分流水
 */
@Data
@TableName("t_points_log")
public class PointsLog implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /** 变动积分数（正=增加，负=扣减） */
    private Integer changePoints;

    private Integer beforePoints;

    private Integer afterPoints;

    /** 类型：1-手动录入 2-消费抵扣 3-取消退回 4-每日清零 */
    private Integer type;

    private String remark;

    private Long orderId;

    private Long adminId;

    private String adminName;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
