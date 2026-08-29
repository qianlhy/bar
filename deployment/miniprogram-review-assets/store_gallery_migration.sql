-- 店内环境图片配置
-- 执行前请先上传 uploads/store/store-env-01.jpg 至服务器 uploads 目录
-- 公网 URL 需返回 HTTP 200

SET NAMES utf8mb4;

INSERT INTO `t_config` (`config_key`, `config_value`, `remark`)
VALUES (
  'store_gallery',
  'https://bar.twst.work/api/uploads/store/store-env-01.jpg',
  '店内环境展示图，多张用英文逗号分隔'
)
ON DUPLICATE KEY UPDATE
  `config_value` = VALUES(`config_value`),
  `remark` = VALUES(`remark`);

SELECT `config_key`, `config_value` FROM `t_config` WHERE `config_key` = 'store_gallery';
