-- 积分 / 员工角色迁移脚本（在已有 bar 库上执行）

-- 1. 用户表增加积分字段
ALTER TABLE `t_user`
  ADD COLUMN `points` int(11) NOT NULL DEFAULT 0 COMMENT '可用积分（每日0点清零）' AFTER `coupon_count`;

-- 2. 订单表增加积分抵扣字段
ALTER TABLE `t_order`
  ADD COLUMN `points_used` int(11) NOT NULL DEFAULT 0 COMMENT '本单使用积分数' AFTER `actual_payment`,
  ADD COLUMN `points_amount` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '积分抵扣金额（元）' AFTER `points_used`;

-- 3. 积分流水表
CREATE TABLE IF NOT EXISTS `t_points_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `change_points` int(11) NOT NULL COMMENT '变动积分数（正=增加，负=扣减）',
  `before_points` int(11) NOT NULL DEFAULT 0 COMMENT '变动前积分',
  `after_points` int(11) NOT NULL DEFAULT 0 COMMENT '变动后积分',
  `type` tinyint(1) NOT NULL COMMENT '类型：1-手动录入 2-消费抵扣 3-取消退回 4-每日清零',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `order_id` bigint(20) DEFAULT NULL COMMENT '关联订单ID',
  `admin_id` bigint(20) DEFAULT NULL COMMENT '操作管理员ID',
  `admin_name` varchar(50) DEFAULT NULL COMMENT '操作管理员昵称',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='积分流水表';

-- 4. 操作锁表（员工录入/查看防冲突）
CREATE TABLE IF NOT EXISTS `t_operation_lock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `lock_key` varchar(100) NOT NULL COMMENT '锁键，如 user:123 / order:456',
  `admin_id` bigint(20) NOT NULL COMMENT '持有锁的管理员ID',
  `admin_name` varchar(50) DEFAULT NULL COMMENT '持有锁的管理员昵称',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lock_key` (`lock_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作锁表';

-- 5. 积分相关配置
INSERT INTO `t_config` (`config_key`, `config_value`, `remark`) VALUES
('points_enabled', '1', '积分抵扣开关：1-开启 0-关闭'),
('points_rate', '100', '积分兑换比例：多少积分抵1元'),
('points_max_ratio', '0.5', '单笔订单积分最多可抵总金额的比例')
ON DUPLICATE KEY UPDATE `config_value` = VALUES(`config_value`);

-- 6. 三个员工账号（密码均为 123456 的 MD5）
-- role: 1-超级管理员 2-普通管理员 3-员工（仅查看订单+录入积分）
INSERT INTO `t_admin` (`username`, `password`, `nickname`, `role`, `status`, `deleted`) VALUES
('staff1', 'e10adc3949ba59abbe56e057f20f883e', '员工一号', 3, 1, 0),
('staff2', 'e10adc3949ba59abbe56e057f20f883e', '员工二号', 3, 1, 0),
('staff3', 'e10adc3949ba59abbe56e057f20f883e', '员工三号', 3, 1, 0)
ON DUPLICATE KEY UPDATE `nickname` = VALUES(`nickname`), `role` = VALUES(`role`), `status` = 1, `deleted` = 0;
