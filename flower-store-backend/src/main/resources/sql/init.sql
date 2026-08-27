-- MySQL dump 10.13  Distrib 8.0.17, for Win64 (x86_64)
--
-- Host: localhost    Database: bar
-- ------------------------------------------------------
-- Server version	8.0.17

CREATE DATABASE IF NOT EXISTS `bar` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `bar`;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `t_address`
--

DROP TABLE IF EXISTS `t_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `phone` varchar(20) NOT NULL COMMENT '收货人手机号',
  `province` varchar(50) NOT NULL COMMENT '省',
  `city` varchar(50) NOT NULL COMMENT '市',
  `district` varchar(50) NOT NULL COMMENT '区',
  `address` varchar(255) NOT NULL COMMENT '详细地址',
  `is_default` tinyint(1) DEFAULT '0' COMMENT '是否默认地址：0-否，1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收货地址表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_address`
--

LOCK TABLES `t_address` WRITE;
/*!40000 ALTER TABLE `t_address` DISABLE KEYS */;
INSERT INTO `t_address` VALUES (1,1,'张三','15888888888','北京市','北京市','朝阳区','XX小区10号楼1单元101',1,'2025-12-22 17:33:07','2025-12-22 17:33:07',0);
/*!40000 ALTER TABLE `t_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_admin`
--

DROP TABLE IF EXISTS `t_admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_admin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `role` tinyint(1) DEFAULT '2' COMMENT '角色：1-超级管理员，2-普通管理员',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='管理员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_admin`
--

LOCK TABLES `t_admin` WRITE;
/*!40000 ALTER TABLE `t_admin` DISABLE KEYS */;
INSERT INTO `t_admin` VALUES (1,'admin','e10adc3949ba59abbe56e057f20f883e','超级管理员',NULL,NULL,1,1,'2025-12-22 17:14:38','2025-12-22 17:14:38',0);
/*!40000 ALTER TABLE `t_admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_cart`
--

DROP TABLE IF EXISTS `t_cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_cart` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `spec_text` varchar(255) NOT NULL DEFAULT '' COMMENT '已选规格',
  `count` int(11) NOT NULL DEFAULT '1' COMMENT '数量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product_spec` (`user_id`,`product_id`,`spec_text`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='购物车表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_cart`
--

LOCK TABLES `t_cart` WRITE;
/*!40000 ALTER TABLE `t_cart` DISABLE KEYS */;
INSERT INTO `t_cart` VALUES (1,1,9,'',1,'2025-12-22 17:32:44','2025-12-22 17:32:44');
/*!40000 ALTER TABLE `t_cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_category`
--

DROP TABLE IF EXISTS `t_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `icon` varchar(255) DEFAULT NULL COMMENT '分类图标',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_category`
--

LOCK TABLES `t_category` WRITE;
/*!40000 ALTER TABLE `t_category` DISABLE KEYS */;
INSERT INTO `t_category` VALUES (1,'开业餐酒畅饮套餐',NULL,1,1,'2025-12-22 17:14:38','2025-12-22 17:20:26',0),(2,'复活畅饮续时套餐',NULL,2,1,'2025-12-22 17:14:38','2025-12-22 17:21:05',0),(3,'下午场畅玩套餐',NULL,3,1,'2025-12-22 17:14:38','2025-12-22 17:21:16',0),(4,'机打精酿',NULL,4,1,'2025-12-22 17:14:38','2025-12-22 17:21:22',0),(5,'27特调',NULL,5,1,'2025-12-22 17:14:38','2025-12-22 17:21:29',0),(6,'瓶啤',NULL,6,1,'2025-12-22 17:14:38','2025-12-22 17:21:34',0),(7,'罐装预调酒',NULL,7,1,'2025-12-22 17:14:38','2025-12-22 17:21:38',0),(8,'单一麦芽威士忌',NULL,8,1,'2025-12-22 17:14:38','2025-12-22 17:21:38',0),(9,'主食',NULL,9,1,'2025-12-22 17:14:38','2025-12-22 17:21:38',0),(10,'冷盘',NULL,10,1,'2025-12-22 17:14:38','2025-12-22 17:21:38',0),(11,'炸食小吃',NULL,11,1,'2025-12-22 17:14:38','2025-12-22 17:21:38',0),(12,'水烟',NULL,12,1,'2025-12-22 17:14:38','2025-12-22 17:21:38',0),(13,'咖啡',NULL,13,1,'2025-12-22 17:14:38','2025-12-22 17:21:38',0),(14,'槟榔',NULL,14,1,'2025-12-22 17:14:38','2025-12-22 17:21:38',0),(15,'零售',NULL,15,1,'2025-12-22 17:14:38','2025-12-22 17:21:38',0);
/*!40000 ALTER TABLE `t_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_favorite`
--

DROP TABLE IF EXISTS `t_favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_favorite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`,`product_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_favorite`
--

LOCK TABLES `t_favorite` WRITE;
/*!40000 ALTER TABLE `t_favorite` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_order`
--

DROP TABLE IF EXISTS `t_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no` varchar(50) NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) NOT NULL COMMENT '收货人手机号',
  `province` varchar(50) DEFAULT NULL COMMENT '收货地址-省',
  `city` varchar(50) DEFAULT NULL COMMENT '收货地址-市',
  `district` varchar(50) DEFAULT NULL COMMENT '收货地址-区',
  `address` varchar(255) NOT NULL COMMENT '详细地址',
  `total_price` decimal(10,2) NOT NULL COMMENT '商品总价',
  `freight` decimal(10,2) DEFAULT '0.00' COMMENT '运费',
  `actual_payment` decimal(10,2) NOT NULL COMMENT '实付金额',
  `payment_method` varchar(20) DEFAULT 'online' COMMENT '支付方式：online-在线支付，delivery-货到付款',
  `remark` varchar(500) DEFAULT NULL COMMENT '订单备注',
  `status` tinyint(1) DEFAULT '1' COMMENT '订单状态：1-待付款，2-待发货，3-已发货，4-已完成，5-已取消',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_order`
--

LOCK TABLES `t_order` WRITE;
/*!40000 ALTER TABLE `t_order` DISABLE KEYS */;
INSERT INTO `t_order` VALUES (1,'ORDER202512221733118289',1,'张三','15888888888','北京市','北京市','朝阳区','XX小区10号楼1单元101',128.00,0.00,128.00,'online','123',4,NULL,NULL,NULL,'2025-12-22 17:33:11','2025-12-22 17:45:03',0);
/*!40000 ALTER TABLE `t_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_order_item`
--

DROP TABLE IF EXISTS `t_order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) NOT NULL COMMENT '商品名称',
  `product_image` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `spec_text` varchar(255) NOT NULL DEFAULT '' COMMENT '下单时选择的规格',
  `price` decimal(10,2) NOT NULL COMMENT '商品价格',
  `count` int(11) NOT NULL COMMENT '购买数量',
  `subtotal` decimal(10,2) NOT NULL COMMENT '小计',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_order_item`
--

LOCK TABLES `t_order_item` WRITE;
/*!40000 ALTER TABLE `t_order_item` DISABLE KEYS */;
INSERT INTO `t_order_item` VALUES (1,1,9,'满天星花束 - 浪漫星空','https://qcloud.dpfile.com/pc/tSUjWiqr9Oe_gr0diUfHNU_5I1tIKBWZYay4biyqeLA57EIn_lnJvPOWlS4lMt0N.jpg','',128.00,1,128.00,'2025-12-22 17:33:11','2025-12-22 17:33:11');
/*!40000 ALTER TABLE `t_order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_product`
--

DROP TABLE IF EXISTS `t_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) NOT NULL COMMENT '商品名称',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `price` decimal(10,2) NOT NULL COMMENT '商品价格',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `inventory` int(11) DEFAULT '0' COMMENT '库存',
  `sales` int(11) DEFAULT '0' COMMENT '销量',
  `rating` decimal(2,1) DEFAULT '5.0' COMMENT '评分',
  `image` varchar(4096) DEFAULT NULL COMMENT '主图',
  `images` text COMMENT '轮播图（多张，逗号分隔）',
  `description` varchar(500) DEFAULT NULL COMMENT '商品描述',
  `detail` text COMMENT '商品详情',
  `specs` text COMMENT '规格选项（JSON格式）',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：0-下架，1-上架',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_product`
--

LOCK TABLES `t_product` WRITE;
/*!40000 ALTER TABLE `t_product` DISABLE KEYS */;
INSERT INTO `t_product` (`id`,`name`,`category_id`,`price`,`original_price`,`inventory`,`sales`,`rating`,`image`,`images`,`description`,`detail`,`specs`,`status`,`create_time`,`update_time`,`deleted`) VALUES
(1,'开业餐酒畅饮新人套餐',1,168.00,268.00,999,798,5.0,'','','每天首次可购买 包含十多种饮料畅饮','每天首次可购买，包含十多种饮料畅饮、三款炸食、4款精酿',NULL,1,'2025-12-22 17:14:38','2025-12-22 17:32:33',0),
(2,'268酒水小食畅饮套餐',1,268.00,NULL,999,381,5.0,'','','包含十多种饮料畅饮三款炸食 4款精酿','包含十多种饮料畅饮、三款炸食、4款精酿',NULL,1,'2025-12-22 17:14:38','2025-12-22 17:32:33',0),
(3,'27畅饮套餐3小时',2,388.00,468.00,999,246,5.0,'','','388续时畅饮3小时套餐 包含畅饮3小时','388续时畅饮3小时套餐，包含畅饮3小时',NULL,1,'2025-12-22 17:14:38','2025-12-22 17:32:33',0),
(4,'27畅饮套餐5小时',2,568.00,598.00,999,142,5.0,'','','568续时畅饮5小时套餐 包含畅饮续时','568续时畅饮5小时套餐，包含畅饮续时',NULL,1,'2025-12-22 17:14:38','2025-12-22 17:32:33',0),
(5,'下午场一小时畅玩套餐',3,128.00,158.00,999,29,5.0,'','','包含多种饮料畅饮三款炸食 以及4款','包含多种饮料畅饮、三款炸食以及4款精酿',NULL,1,'2025-12-22 17:14:38','2025-12-22 17:32:33',0),
(6,'BLUFF',5,68.00,NULL,999994,5,5.0,'','','姜汁菠萝','姜汁菠萝特调，口感清爽',NULL,1,'2025-12-22 17:14:38','2025-12-22 17:32:33',0),
(7,'OUTS',5,68.00,NULL,999,3,5.0,'','','草莓乳酸菌','草莓乳酸菌特调',NULL,1,'2025-12-22 17:14:38','2025-12-22 17:32:33',0),
(8,'林德曼（樱桃/桃子）',6,40.00,NULL,999,5,5.0,'','','比利时果味精酿啤酒','林德曼果味精酿，樱桃/桃子双口味可选','[{\"name\":\"口味\",\"options\":[{\"label\":\"樱桃\",\"price\":40},{\"label\":\"桃子\",\"price\":40}]}]',1,'2025-12-22 17:14:38','2025-12-22 17:32:33',0),
(9,'1664（桃红/百香果/法蓝）',6,20.00,NULL,999,2,5.0,'','','法国进口果味啤酒','1664果味啤酒，三种口味可选','[{\"name\":\"口味\",\"options\":[{\"label\":\"桃红\",\"price\":20},{\"label\":\"百香果\",\"price\":20},{\"label\":\"法蓝\",\"price\":20}]}]',1,'2025-12-22 17:14:38','2025-12-22 17:32:33',0),
(10,'诱惑7号',6,30.00,NULL,999,14,5.0,'','','比利时烈性啤酒','诱惑7号烈性精酿',NULL,1,'2025-12-22 17:14:38','2025-12-22 17:32:33',0),
(11,'大星',6,30.00,NULL,999,4,5.0,'','','经典瓶装啤酒','大星瓶啤',NULL,1,'2025-12-22 17:14:38','2025-12-22 17:32:33',0);
/*!40000 ALTER TABLE `t_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user`
--

DROP TABLE IF EXISTS `t_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `openid` varchar(100) DEFAULT NULL COMMENT '微信OpenID',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `password` varchar(100) DEFAULT NULL COMMENT '密码（MD5加密）',
  `gender` tinyint(1) DEFAULT '0' COMMENT '性别：0-未知，1-男，2-女',
  `user_type` tinyint(1) DEFAULT '1' COMMENT '用户类型：1-普通用户，2-管理员',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `balance` decimal(10,2) DEFAULT '0.00' COMMENT '账户余额',
  `coins` int(11) DEFAULT '0' COMMENT 'All In币',
  `master_score` int(11) DEFAULT '0' COMMENT '大师分',
  `coupon_count` int(11) DEFAULT '0' COMMENT '优惠券数量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user`
--

LOCK TABLES `t_user` WRITE;
/*!40000 ALTER TABLE `t_user` DISABLE KEYS */;
INSERT INTO `t_user` VALUES (1,'ol8nk6J5xCFComcE0TLbkkAxhmec','liu','https://bar.twst.work/api/uploads/2025/12/22/1766397025827_0rka2b69q14.jpeg',NULL,NULL,NULL,1,1,20.00,0,0,0,'2025-12-22 17:31:26','2025-12-22 17:50:27',0);
/*!40000 ALTER TABLE `t_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Demo ranking users（大师分排行榜演示数据）
--

INSERT INTO `t_user` (`id`,`openid`,`nickname`,`avatar`,`gender`,`user_type`,`status`,`balance`,`coins`,`master_score`,`coupon_count`,`create_time`,`update_time`,`deleted`) VALUES
(2,'demo_openid_2','饭糊了',NULL,1,1,1,0.00,0,23,0,'2025-12-22 17:31:26','2025-12-22 17:31:26',0),
(3,'demo_openid_3','xq',NULL,1,1,1,0.00,0,22,0,'2025-12-22 17:31:26','2025-12-22 17:31:26',0),
(4,'demo_openid_4','睡觉了',NULL,1,1,1,0.00,0,16,0,'2025-12-22 17:31:26','2025-12-22 17:31:26',0),
(5,'demo_openid_5','wojsnx',NULL,1,1,1,0.00,0,16,0,'2025-12-22 17:31:26','2025-12-22 17:31:26',0),
(6,'demo_openid_6','不偷铁头娃',NULL,1,1,1,0.00,0,15,0,'2025-12-22 17:31:26','2025-12-22 17:31:26',0),
(7,'demo_openid_7','1747',NULL,1,1,1,0.00,0,15,0,'2025-12-22 17:31:26','2025-12-22 17:31:26',0),
(8,'demo_openid_8','张',NULL,1,1,1,0.00,0,13,0,'2025-12-22 17:31:26','2025-12-22 17:31:26',0),
(9,'demo_openid_9','再见孙悟空',NULL,1,1,1,0.00,0,12,0,'2025-12-22 17:31:26','2025-12-22 17:31:26',0),
(10,'demo_openid_10','z',NULL,1,1,1,0.00,0,10,0,'2025-12-22 17:31:26','2025-12-22 17:31:26',0);

--
-- Table structure for table `t_coin_product`（All In币商品表）
--

DROP TABLE IF EXISTS `t_coin_product`;
CREATE TABLE `t_coin_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) NOT NULL COMMENT '商品名称',
  `image` varchar(4096) DEFAULT NULL COMMENT '商品图片',
  `coin_price` int(11) NOT NULL COMMENT '兑换所需All In币',
  `stock` int(11) DEFAULT '0' COMMENT '库存',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：0-下架，1-上架',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='All In币商品表';

LOCK TABLES `t_coin_product` WRITE;
INSERT INTO `t_coin_product` VALUES
(1,'精酿啤酒一杯','',50,100,1,1,'2025-12-22 17:14:38','2025-12-22 17:14:38',0),
(2,'27特调一杯','',80,50,2,1,'2025-12-22 17:14:38','2025-12-22 17:14:38',0),
(3,'招牌炸食小食','',60,80,3,1,'2025-12-22 17:14:38','2025-12-22 17:14:38',0),
(4,'梭哈酒馆定制周边','',200,20,4,1,'2025-12-22 17:14:38','2025-12-22 17:14:38',0);
UNLOCK TABLES;

--
-- Table structure for table `t_coin_exchange`（All In币兑换记录表）
--

DROP TABLE IF EXISTS `t_coin_exchange`;
CREATE TABLE `t_coin_exchange` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `coin_product_id` bigint(20) NOT NULL COMMENT 'All In币商品ID',
  `product_name` varchar(100) DEFAULT NULL COMMENT '商品名称（快照）',
  `coin_price` int(11) NOT NULL COMMENT '消耗All In币',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='All In币兑换记录表';

--
-- Table structure for table `t_recharge_package`（会员充值套餐表）
--

DROP TABLE IF EXISTS `t_recharge_package`;
CREATE TABLE `t_recharge_package` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) NOT NULL COMMENT '套餐名称',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
  `balance` decimal(10,2) NOT NULL COMMENT '到账余额',
  `gift_coins` int(11) DEFAULT '0' COMMENT '赠送All In币',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：0-下架，1-上架',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会员充值套餐表';

LOCK TABLES `t_recharge_package` WRITE;
INSERT INTO `t_recharge_package` VALUES
(1,'充100送20',100.00,120.00,0,1,1,'2025-12-22 17:14:38','2025-12-22 17:14:38',0),
(2,'充300送80',300.00,380.00,50,2,1,'2025-12-22 17:14:38','2025-12-22 17:14:38',0),
(3,'充500送150',500.00,650.00,100,3,1,'2025-12-22 17:14:38','2025-12-22 17:14:38',0);
UNLOCK TABLES;

--
-- Table structure for table `t_recharge_order`（会员充值订单表）
--

DROP TABLE IF EXISTS `t_recharge_order`;
CREATE TABLE `t_recharge_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no` varchar(50) NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `package_id` bigint(20) DEFAULT NULL COMMENT '套餐ID',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
  `balance` decimal(10,2) NOT NULL COMMENT '到账余额',
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

--
-- Table structure for table `t_config`（系统配置表：门店信息 / WiFi 等）
--

DROP TABLE IF EXISTS `t_config`;
CREATE TABLE `t_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_value` varchar(1000) DEFAULT NULL COMMENT '配置值',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统配置表';

LOCK TABLES `t_config` WRITE;
INSERT INTO `t_config` VALUES
(1,'store_name','梭哈酒馆 - 南京店','门店名称'),
(2,'store_address','江苏省南京市浦口区江浦街道明发新城中心2栋4单元1007','门店地址'),
(3,'store_phone','','门店电话'),
(4,'wifi_name','AllInTavern','WiFi名称'),
(5,'wifi_password','66668888','WiFi密码'),
(6,'recharge_tip','享受更多专属优惠福利','充值说明'),
(7,'points_enabled','1','积分抵扣开关：1-开启 0-关闭'),
(8,'points_rate','100','积分兑换比例：多少积分抵1元'),
(9,'points_max_ratio','0.5','单笔订单积分最多可抵总金额的比例');
UNLOCK TABLES;

--
-- Dumping routines for database 'bar'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-22 17:52:18
