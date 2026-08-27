-- 充值套餐 + 三种支付方式迁移
-- 适用：MySQL 5.7+/8.0
-- 规则：充值到账全部进入 All In 币 = 实付金额(元取整) + 赠送币；点单 1 币 = 1 元

SET NAMES utf8mb4;

-- ---------------------------------------------------------------------------
-- 1) 充值套餐表（若不存在则创建）
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `t_recharge_package` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) NOT NULL COMMENT '套餐名称',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
  `balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '兼容旧字段，点单支付不再使用余额',
  `gift_coins` int(11) DEFAULT '0' COMMENT '赠送All In币',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：0-下架，1-上架',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会员充值套餐表';

CREATE TABLE IF NOT EXISTS `t_recharge_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no` varchar(50) NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `package_id` bigint(20) DEFAULT NULL COMMENT '套餐ID',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
  `balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '兼容旧字段',
  `gift_coins` int(11) DEFAULT '0' COMMENT '赠送All In币',
  `pay_method` varchar(20) DEFAULT 'wechat' COMMENT '支付方式',
  `status` tinyint(1) DEFAULT '0' COMMENT '状态：0-待支付，1-已到账，2-已取消',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会员充值订单表';

-- ---------------------------------------------------------------------------
-- 2) 订单表增加币支付字段（幂等：列已存在则跳过）
-- ---------------------------------------------------------------------------
SET @db := DATABASE();

SET @sql := (
  SELECT IF(
    COUNT(*) = 0,
    CONCAT(
      'ALTER TABLE `t_order` ADD COLUMN `coins_used` int(11) DEFAULT 0 COMMENT ''本单使用All In币数'' AFTER `',
      IF(
        (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = @db AND table_name = 't_order' AND column_name = 'points_amount') > 0,
        'points_amount',
        'actual_payment'
      ),
      '`'
    ),
    'SELECT 1'
  )
  FROM information_schema.columns
  WHERE table_schema = @db AND table_name = 't_order' AND column_name = 'coins_used'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE `t_order` ADD COLUMN `coins_amount` decimal(10,2) DEFAULT 0.00 COMMENT ''币抵扣金额(元)'' AFTER `coins_used`',
    'SELECT 1'
  )
  FROM information_schema.columns
  WHERE table_schema = @db AND table_name = 't_order' AND column_name = 'coins_amount'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE `t_order` ADD COLUMN `wechat_amount` decimal(10,2) DEFAULT NULL COMMENT ''微信支付金额(元)'' AFTER `coins_amount`',
    'SELECT 1'
  )
  FROM information_schema.columns
  WHERE table_schema = @db AND table_name = 't_order' AND column_name = 'wechat_amount'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ---------------------------------------------------------------------------
-- 3) All In 币流水表
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `t_coin_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `change_coins` int(11) NOT NULL COMMENT '变动币数，正加负减',
  `before_coins` int(11) NOT NULL DEFAULT '0' COMMENT '变动前',
  `after_coins` int(11) NOT NULL DEFAULT '0' COMMENT '变动后',
  `type` varchar(32) NOT NULL COMMENT '类型：recharge/pay/refund/adjust',
  `ref_no` varchar(64) DEFAULT NULL COMMENT '关联单号',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_ref_no` (`ref_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='All In币流水';

-- ---------------------------------------------------------------------------
-- 4) 写入四档充值套餐（旧套餐下架，新套餐按名称幂等更新）
-- ---------------------------------------------------------------------------
UPDATE `t_recharge_package`
SET `status` = 0
WHERE `deleted` = 0
  AND `name` NOT IN ('充500送288', '充1000送688', '充2000送1288', '充3000送1888');

INSERT INTO `t_recharge_package`
  (`name`, `pay_amount`, `balance`, `gift_coins`, `sort`, `status`, `deleted`)
VALUES
  ('充500送288', 500.00, 0.00, 288, 1, 1, 0),
  ('充1000送688', 1000.00, 0.00, 688, 2, 1, 0),
  ('充2000送1288', 2000.00, 0.00, 1288, 3, 1, 0),
  ('充3000送1888', 3000.00, 0.00, 1888, 4, 1, 0)
ON DUPLICATE KEY UPDATE
  `pay_amount` = VALUES(`pay_amount`),
  `balance` = VALUES(`balance`),
  `gift_coins` = VALUES(`gift_coins`),
  `sort` = VALUES(`sort`),
  `status` = 1,
  `deleted` = 0;

-- 无唯一键时上面 ON DUPLICATE 可能无效，再按名称更新/补插
UPDATE `t_recharge_package`
SET `pay_amount` = 500.00, `balance` = 0.00, `gift_coins` = 288, `sort` = 1, `status` = 1, `deleted` = 0
WHERE `name` = '充500送288';

UPDATE `t_recharge_package`
SET `pay_amount` = 1000.00, `balance` = 0.00, `gift_coins` = 688, `sort` = 2, `status` = 1, `deleted` = 0
WHERE `name` = '充1000送688';

UPDATE `t_recharge_package`
SET `pay_amount` = 2000.00, `balance` = 0.00, `gift_coins` = 1288, `sort` = 3, `status` = 1, `deleted` = 0
WHERE `name` = '充2000送1288';

UPDATE `t_recharge_package`
SET `pay_amount` = 3000.00, `balance` = 0.00, `gift_coins` = 1888, `sort` = 4, `status` = 1, `deleted` = 0
WHERE `name` = '充3000送1888';

INSERT INTO `t_recharge_package`
  (`name`, `pay_amount`, `balance`, `gift_coins`, `sort`, `status`, `deleted`)
SELECT '充500送288', 500.00, 0.00, 288, 1, 1, 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `t_recharge_package` WHERE `name` = '充500送288' AND `deleted` = 0);

INSERT INTO `t_recharge_package`
  (`name`, `pay_amount`, `balance`, `gift_coins`, `sort`, `status`, `deleted`)
SELECT '充1000送688', 1000.00, 0.00, 688, 2, 1, 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `t_recharge_package` WHERE `name` = '充1000送688' AND `deleted` = 0);

INSERT INTO `t_recharge_package`
  (`name`, `pay_amount`, `balance`, `gift_coins`, `sort`, `status`, `deleted`)
SELECT '充2000送1288', 2000.00, 0.00, 1288, 3, 1, 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `t_recharge_package` WHERE `name` = '充2000送1288' AND `deleted` = 0);

INSERT INTO `t_recharge_package`
  (`name`, `pay_amount`, `balance`, `gift_coins`, `sort`, `status`, `deleted`)
SELECT '充3000送1888', 3000.00, 0.00, 1888, 4, 1, 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `t_recharge_package` WHERE `name` = '充3000送1888' AND `deleted` = 0);

-- 核验
SELECT `id`, `name`, `pay_amount`, `gift_coins`, (`pay_amount` + `gift_coins`) AS `credit_coins`, `status`
FROM `t_recharge_package`
WHERE `deleted` = 0 AND `status` = 1
ORDER BY `sort`, `id`;
