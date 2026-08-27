-- 小程序提审上线数据包
-- 适用：MySQL 5.7+/8.0
-- 注意：本文件不包含、也不依赖 commerce_fix_migration.sql。
-- 执行前请先上传 uploads/products/product-01.jpg 至 product-11.jpg，
-- 并逐一确认公网 URL 返回 HTTP 200。
--
-- 兼容说明：若线上不存在 t_recharge_package / t_coin_product，
-- 对应备份、下架与核验会自动跳过，不会报错，也不会新建这两张业务表。

SET NAMES utf8mb4;

-- 首次执行时保存原值。INSERT IGNORE 确保重复执行不会覆盖最初备份。
CREATE TABLE IF NOT EXISTS `t_miniprogram_review_backup` (
  `object_type` varchar(32) NOT NULL,
  `object_id` bigint(20) NOT NULL,
  `image` varchar(4096) DEFAULT NULL,
  `images` text,
  `status` tinyint(1) DEFAULT NULL,
  `backup_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`object_type`, `object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
  COMMENT='小程序提审上线前原值备份';

-- MySQL 的 DDL 会隐式提交，因此必须在建表完成后再开启数据事务。
START TRANSACTION;

INSERT IGNORE INTO `t_miniprogram_review_backup`
  (`object_type`, `object_id`, `image`, `images`, `status`)
SELECT 'product', `id`, `image`, `images`, `status`
FROM `t_product`
WHERE (`id` = 1 AND `name` = '开业餐酒畅饮新人套餐')
   OR (`id` = 2 AND `name` = '268酒水小食畅饮套餐')
   OR (`id` = 3 AND `name` = '27畅饮套餐3小时')
   OR (`id` = 4 AND `name` = '27畅饮套餐5小时')
   OR (`id` = 5 AND `name` = '下午场一小时畅玩套餐')
   OR (`id` = 6 AND `name` = 'BLUFF')
   OR (`id` = 7 AND `name` = 'OUTS')
   OR (`id` = 8 AND `name` = '林德曼（樱桃/桃子）')
   OR (`id` = 9 AND `name` = '1664（桃红/百香果/法蓝）')
   OR (`id` = 10 AND `name` = '诱惑7号')
   OR (`id` = 11 AND `name` = '大星');

INSERT IGNORE INTO `t_miniprogram_review_backup`
  (`object_type`, `object_id`, `status`)
SELECT 'category', `id`, `status`
FROM `t_category`
WHERE (`id` = 12 AND `name` = '水烟')
   OR (`id` = 14 AND `name` = '槟榔');

INSERT IGNORE INTO `t_miniprogram_review_backup`
  (`object_type`, `object_id`, `status`)
SELECT 'restricted_product', p.`id`, p.`status`
FROM `t_product` p
INNER JOIN `t_category` c ON c.`id` = p.`category_id`
WHERE (c.`id` = 12 AND c.`name` = '水烟')
   OR (c.`id` = 14 AND c.`name` = '槟榔');

-- 仅当表存在时才备份/下架，避免线上缺表时报 #1146。
SET @has_coin_product := (
  SELECT COUNT(*)
  FROM information_schema.tables
  WHERE table_schema = DATABASE()
    AND table_name = 't_coin_product'
);

SET @sql_coin_backup := IF(
  @has_coin_product > 0,
  'INSERT IGNORE INTO `t_miniprogram_review_backup` (`object_type`, `object_id`, `status`) SELECT ''coin_product'', `id`, `status` FROM `t_coin_product`',
  'SELECT 1'
);
PREPARE stmt FROM @sql_coin_backup;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_recharge_package := (
  SELECT COUNT(*)
  FROM information_schema.tables
  WHERE table_schema = DATABASE()
    AND table_name = 't_recharge_package'
);

SET @sql_recharge_backup := IF(
  @has_recharge_package > 0,
  'INSERT IGNORE INTO `t_miniprogram_review_backup` (`object_type`, `object_id`, `status`) SELECT ''recharge_package'', `id`, `status` FROM `t_recharge_package`',
  'SELECT 1'
);
PREPARE stmt FROM @sql_recharge_backup;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 仅当 ID 与线上商品名同时匹配时更新，避免误改其他环境中的同 ID 数据。
UPDATE `t_product`
SET
  `image` = CASE `id`
    WHEN 1 THEN 'https://bar.twst.work/api/uploads/products/product-01.jpg'
    WHEN 2 THEN 'https://bar.twst.work/api/uploads/products/product-02.jpg'
    WHEN 3 THEN 'https://bar.twst.work/api/uploads/products/product-03.jpg'
    WHEN 4 THEN 'https://bar.twst.work/api/uploads/products/product-04.jpg'
    WHEN 5 THEN 'https://bar.twst.work/api/uploads/products/product-05.jpg'
    WHEN 6 THEN 'https://bar.twst.work/api/uploads/products/product-06.jpg'
    WHEN 7 THEN 'https://bar.twst.work/api/uploads/products/product-07.jpg'
    WHEN 8 THEN 'https://bar.twst.work/api/uploads/products/product-08.jpg'
    WHEN 9 THEN 'https://bar.twst.work/api/uploads/products/product-09.jpg'
    WHEN 10 THEN 'https://bar.twst.work/api/uploads/products/product-10.jpg'
    WHEN 11 THEN 'https://bar.twst.work/api/uploads/products/product-11.jpg'
  END,
  `images` = CASE `id`
    WHEN 1 THEN 'https://bar.twst.work/api/uploads/products/product-01.jpg'
    WHEN 2 THEN 'https://bar.twst.work/api/uploads/products/product-02.jpg'
    WHEN 3 THEN 'https://bar.twst.work/api/uploads/products/product-03.jpg'
    WHEN 4 THEN 'https://bar.twst.work/api/uploads/products/product-04.jpg'
    WHEN 5 THEN 'https://bar.twst.work/api/uploads/products/product-05.jpg'
    WHEN 6 THEN 'https://bar.twst.work/api/uploads/products/product-06.jpg'
    WHEN 7 THEN 'https://bar.twst.work/api/uploads/products/product-07.jpg'
    WHEN 8 THEN 'https://bar.twst.work/api/uploads/products/product-08.jpg'
    WHEN 9 THEN 'https://bar.twst.work/api/uploads/products/product-09.jpg'
    WHEN 10 THEN 'https://bar.twst.work/api/uploads/products/product-10.jpg'
    WHEN 11 THEN 'https://bar.twst.work/api/uploads/products/product-11.jpg'
  END
WHERE `deleted` = 0
  AND (
       (`id` = 1 AND `name` = '开业餐酒畅饮新人套餐')
    OR (`id` = 2 AND `name` = '268酒水小食畅饮套餐')
    OR (`id` = 3 AND `name` = '27畅饮套餐3小时')
    OR (`id` = 4 AND `name` = '27畅饮套餐5小时')
    OR (`id` = 5 AND `name` = '下午场一小时畅玩套餐')
    OR (`id` = 6 AND `name` = 'BLUFF')
    OR (`id` = 7 AND `name` = 'OUTS')
    OR (`id` = 8 AND `name` = '林德曼（樱桃/桃子）')
    OR (`id` = 9 AND `name` = '1664（桃红/百香果/法蓝）')
    OR (`id` = 10 AND `name` = '诱惑7号')
    OR (`id` = 11 AND `name` = '大星')
  );

-- 下架无对应资质的分类，并兜底下架其商品。
UPDATE `t_category`
SET `status` = 0
WHERE (`id` = 12 AND `name` = '水烟')
   OR (`id` = 14 AND `name` = '槟榔');

UPDATE `t_product`
SET `status` = 0
WHERE `category_id` IN (
  SELECT `id`
  FROM `t_category`
  WHERE (`id` = 12 AND `name` = '水烟')
     OR (`id` = 14 AND `name` = '槟榔')
);

-- 无单用途预付卡备案：仅当对应表存在时才下架。
SET @sql_recharge_off := IF(
  @has_recharge_package > 0,
  'UPDATE `t_recharge_package` SET `status` = 0 WHERE `deleted` = 0',
  'SELECT 1'
);
PREPARE stmt FROM @sql_recharge_off;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql_coin_off := IF(
  @has_coin_product > 0,
  'UPDATE `t_coin_product` SET `status` = 0 WHERE `deleted` = 0',
  'SELECT 1'
);
PREPARE stmt FROM @sql_coin_off;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

COMMIT;

-- 执行后核验：
-- 1) 11 条商品应有 image / images
-- 2) 水烟、槟榔分类/商品应无仍在上架记录
-- 3) 充值/币表若不存在，会显示 skipped，不算失败
SELECT `id`, `name`, `image`, `images`
FROM `t_product`
WHERE `id` BETWEEN 1 AND 11 AND `deleted` = 0
ORDER BY `id`;

SELECT `id`, `name`, `status`
FROM `t_category`
WHERE `id` IN (12, 14) AND `status` <> 0;

SELECT `id`, `name`, `status`
FROM `t_product`
WHERE `category_id` IN (12, 14) AND `deleted` = 0 AND `status` <> 0;

SET @sql_recharge_check := IF(
  @has_recharge_package > 0,
  'SELECT `id`, `name`, `status` FROM `t_recharge_package` WHERE `deleted` = 0 AND `status` <> 0',
  'SELECT ''t_recharge_package'' AS `table_name`, ''skipped_missing_table'' AS `result`'
);
PREPARE stmt FROM @sql_recharge_check;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql_coin_check := IF(
  @has_coin_product > 0,
  'SELECT `id`, `name`, `status` FROM `t_coin_product` WHERE `deleted` = 0 AND `status` <> 0',
  'SELECT ''t_coin_product'' AS `table_name`, ''skipped_missing_table'' AS `result`'
);
PREPARE stmt FROM @sql_coin_check;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
