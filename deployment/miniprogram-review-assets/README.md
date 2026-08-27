# 小程序提审上线包

本目录与 `commerce_fix_migration.sql` 无关，不要混合执行。

## 上线顺序

1. 将本目录的 `uploads/products` 整个上传到服务器现有 `uploads/` 根目录，最终文件应位于 `uploads/products/product-01.jpg` 至 `product-11.jpg`。
2. 在浏览器或服务器上逐一确认以下 11 个地址均返回 HTTP 200 且 Content-Type 为图片：
   `https://bar.twst.work/api/uploads/products/product-01.jpg` 至
   `https://bar.twst.work/api/uploads/products/product-11.jpg`。
3. 先备份线上数据库，再执行 `miniprogram_review_launch.sql`。该脚本自身也会把本次涉及行的原值保存到 `t_miniprogram_review_backup`。
4. 检查脚本末尾的结果集：
   - 11 个商品均有 `image` 和 `images`；
   - 水烟、槟榔分类无仍在上架的记录；
   - 两类商品无仍在上架的记录；
   - 充值套餐和 All In 币商品无仍在上架的记录。
5. 请求公开接口验收：
   - `/api/product/list` 返回的 11 个商品图片地址正确；
   - 分类接口不再返回水烟、槟榔；
   - 充值套餐和 All In 币商品公开列表为空。

## 回滚

如数据库结果不符合预期，立即执行 `miniprogram_review_rollback.sql`。它只恢复上线脚本首次保存的原值，不会猜测或重置其他数据。

确认回滚结果无误前，不要删除 `t_miniprogram_review_backup`。图片文件可在数据库回滚后再从服务器移除。

## 安全说明

- 上线 SQL 对 11 个商品同时校验固定 ID 与商品名，避免在数据不一致的环境误更新同 ID 商品。
- 下架敏感分类时同时校验分类 ID 与名称，并同步兜底下架分类内商品。
- 若线上不存在 `t_recharge_package` 或 `t_coin_product`，脚本会自动跳过相关备份/下架/核验，不会报 `#1146`，也不会新建这两张业务表。
- 脚本可重复执行；首次备份通过 `INSERT IGNORE` 保留，不会被后续执行覆盖。
- SQL 不会执行或合并 `commerce_fix_migration.sql`。
