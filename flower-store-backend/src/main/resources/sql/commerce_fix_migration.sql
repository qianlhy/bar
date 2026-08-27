-- 商用链路修复迁移：购物车规格与订单规格
-- 在已有 bar 库部署新版 JAR 前执行一次

ALTER TABLE `t_cart`
  ADD COLUMN `spec_text` varchar(255) NOT NULL DEFAULT '' COMMENT '已选规格' AFTER `product_id`,
  DROP INDEX `uk_user_product`,
  ADD UNIQUE KEY `uk_user_product_spec` (`user_id`, `product_id`, `spec_text`);

ALTER TABLE `t_order_item`
  ADD COLUMN `spec_text` varchar(255) NOT NULL DEFAULT '' COMMENT '下单时选择的规格' AFTER `product_image`;
