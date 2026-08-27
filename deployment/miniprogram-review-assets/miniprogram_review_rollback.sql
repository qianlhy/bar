-- 回滚 miniprogram_review_launch.sql 保存的原值。
-- 仅在上线 SQL 已成功执行并存在 t_miniprogram_review_backup 时使用。
-- 若线上不存在 t_recharge_package / t_coin_product，对应回滚会自动跳过。

SET NAMES utf8mb4;
START TRANSACTION;

UPDATE `t_product` p
INNER JOIN `t_miniprogram_review_backup` b
  ON b.`object_type` = 'product' AND b.`object_id` = p.`id`
SET p.`image` = b.`image`,
    p.`images` = b.`images`,
    p.`status` = b.`status`;

UPDATE `t_product` p
INNER JOIN `t_miniprogram_review_backup` b
  ON b.`object_type` = 'restricted_product' AND b.`object_id` = p.`id`
SET p.`status` = b.`status`;

UPDATE `t_category` c
INNER JOIN `t_miniprogram_review_backup` b
  ON b.`object_type` = 'category' AND b.`object_id` = c.`id`
SET c.`status` = b.`status`;

SET @has_coin_product := (
  SELECT COUNT(*)
  FROM information_schema.tables
  WHERE table_schema = DATABASE()
    AND table_name = 't_coin_product'
);

SET @sql_coin_rollback := IF(
  @has_coin_product > 0,
  'UPDATE `t_coin_product` p INNER JOIN `t_miniprogram_review_backup` b ON b.`object_type` = ''coin_product'' AND b.`object_id` = p.`id` SET p.`status` = b.`status`',
  'SELECT 1'
);
PREPARE stmt FROM @sql_coin_rollback;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_recharge_package := (
  SELECT COUNT(*)
  FROM information_schema.tables
  WHERE table_schema = DATABASE()
    AND table_name = 't_recharge_package'
);

SET @sql_recharge_rollback := IF(
  @has_recharge_package > 0,
  'UPDATE `t_recharge_package` p INNER JOIN `t_miniprogram_review_backup` b ON b.`object_type` = ''recharge_package'' AND b.`object_id` = p.`id` SET p.`status` = b.`status`',
  'SELECT 1'
);
PREPARE stmt FROM @sql_recharge_rollback;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

COMMIT;

-- 确认恢复结果后才可手工删除备份表。
SELECT `object_type`, COUNT(*) AS `backup_rows`
FROM `t_miniprogram_review_backup`
GROUP BY `object_type`
ORDER BY `object_type`;
