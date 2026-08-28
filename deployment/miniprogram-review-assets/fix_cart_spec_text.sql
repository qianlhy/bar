-- 修复：线上 t_cart / t_order_item 缺少 spec_text（新 jar 已依赖该字段）
-- 适用：MySQL 5.7+/8.0
-- 可重复执行

SET NAMES utf8mb4;
SET @db := DATABASE();

-- t_cart.spec_text
SET @sql := (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE `t_cart` ADD COLUMN `spec_text` varchar(255) NOT NULL DEFAULT '''' COMMENT ''已选规格'' AFTER `product_id`',
    'SELECT 1'
  )
  FROM information_schema.columns
  WHERE table_schema = @db AND table_name = 't_cart' AND column_name = 'spec_text'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- t_cart 旧唯一键（仅 user+product）若存在则删除，避免与规格唯一键冲突
SET @sql := (
  SELECT IF(
    COUNT(*) > 0,
    'ALTER TABLE `t_cart` DROP INDEX `uk_user_product`',
    'SELECT 1'
  )
  FROM information_schema.statistics
  WHERE table_schema = @db AND table_name = 't_cart' AND index_name = 'uk_user_product'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- t_cart 唯一键（按用户+商品+规格）
SET @sql := (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE `t_cart` ADD UNIQUE KEY `uk_user_product_spec` (`user_id`, `product_id`, `spec_text`)',
    'SELECT 1'
  )
  FROM information_schema.statistics
  WHERE table_schema = @db AND table_name = 't_cart' AND index_name = 'uk_user_product_spec'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- t_order_item.spec_text（下单也会用到，一并补上避免下一步再炸）
SET @sql := (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE `t_order_item` ADD COLUMN `spec_text` varchar(255) NOT NULL DEFAULT '''' COMMENT ''下单时选择的规格'' AFTER `product_image`',
    'SELECT 1'
  )
  FROM information_schema.columns
  WHERE table_schema = @db AND table_name = 't_order_item' AND column_name = 'spec_text'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SELECT 'cart_order_item_spec_ok' AS result;
