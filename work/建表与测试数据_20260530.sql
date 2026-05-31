-- 2026-05-30: smart-container 本地联调建表与测试数据脚本
CREATE DATABASE IF NOT EXISTS `smart_container`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE `smart_container`;

CREATE TABLE IF NOT EXISTS `customer` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` VARCHAR(64) NOT NULL COMMENT '客户ID',
  `user_name` VARCHAR(64) DEFAULT NULL COMMENT '姓名',
  `wechat_open_id` VARCHAR(128) DEFAULT NULL COMMENT '微信openId',
  `ali_open_id` VARCHAR(128) DEFAULT NULL COMMENT '支付宝openId',
  `mobile` VARCHAR(32) DEFAULT NULL COMMENT '手机号',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remarks` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_customer_id` (`customer_id`),
  KEY `idx_customer_ali_open_id` (`ali_open_id`),
  KEY `idx_customer_mobile` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户信息表';

CREATE TABLE IF NOT EXISTS `customer_agreement_sign` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` VARCHAR(64) NOT NULL COMMENT '客户ID',
  `status` VARCHAR(32) NOT NULL COMMENT '签约状态',
  `order_no` VARCHAR(64) DEFAULT NULL COMMENT '订单号',
  `agreement_no` VARCHAR(128) DEFAULT NULL COMMENT '签约协议号',
  `sign_time` DATETIME DEFAULT NULL COMMENT '签约时间',
  `ali_open_id` VARCHAR(128) DEFAULT NULL COMMENT '支付宝openId',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_agreement_no` (`agreement_no`),
  KEY `idx_agreement_customer_id` (`customer_id`),
  KEY `idx_agreement_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户签约信息表';

CREATE TABLE IF NOT EXISTS `verify_code` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `mobile` VARCHAR(32) NOT NULL COMMENT '手机号',
  `verify_code` VARCHAR(16) NOT NULL COMMENT '验证码',
  `order_no` VARCHAR(64) DEFAULT NULL COMMENT '订单号',
  `retry_times` INT NOT NULL DEFAULT 0 COMMENT '重试次数',
  `invalid_time` DATETIME DEFAULT NULL COMMENT '失效时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_verify_mobile` (`mobile`),
  KEY `idx_verify_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='验证码表';

CREATE TABLE IF NOT EXISTS `device_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `device_id` VARCHAR(64) NOT NULL COMMENT '设备ID',
  `device_sn` VARCHAR(64) NOT NULL COMMENT '设备SN',
  `device_name` VARCHAR(128) DEFAULT NULL COMMENT '设备名称',
  `device_status` VARCHAR(32) NOT NULL DEFAULT 'INIT' COMMENT '设备状态',
  `device_type` VARCHAR(32) DEFAULT NULL COMMENT '设备类型',
  `heating_switch_status` VARCHAR(32) DEFAULT NULL COMMENT '加热开关',
  `heating_threshold` INT DEFAULT NULL COMMENT '加热阈值',
  `close_heating_threshold` INT DEFAULT NULL COMMENT '关闭加热阈值',
  `province` VARCHAR(64) DEFAULT NULL COMMENT '省',
  `city` VARCHAR(64) DEFAULT NULL COMMENT '市',
  `county` VARCHAR(64) DEFAULT NULL COMMENT '区县',
  `community` VARCHAR(128) DEFAULT NULL COMMENT '社区',
  `address` VARCHAR(255) DEFAULT NULL COMMENT '详细地址',
  `device_desc` VARCHAR(500) DEFAULT NULL COMMENT '设备描述',
  `lgt_on_time` VARCHAR(64) DEFAULT NULL COMMENT '灯光开启时间',
  `addr_location_gd` VARCHAR(128) DEFAULT NULL COMMENT '高德经纬度',
  `addr_location_tx` VARCHAR(128) DEFAULT NULL COMMENT '腾讯经纬度',
  `device_version` VARCHAR(64) DEFAULT NULL COMMENT '设备版本号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remarks` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_id` (`device_id`),
  UNIQUE KEY `uk_device_sn` (`device_sn`),
  KEY `idx_device_status` (`device_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备信息表';

CREATE TABLE IF NOT EXISTS `gate_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `gate_id` VARCHAR(64) NOT NULL COMMENT '仓门ID',
  `device_id` VARCHAR(64) NOT NULL COMMENT '设备ID',
  `device_sn` VARCHAR(64) NOT NULL COMMENT '设备SN',
  `gate_name` VARCHAR(128) DEFAULT NULL COMMENT '仓门名称',
  `gate_code` VARCHAR(64) DEFAULT NULL COMMENT '仓门代码',
  `sort` INT DEFAULT NULL COMMENT '排序',
  `gate_status` VARCHAR(32) NOT NULL DEFAULT 'INIT' COMMENT '仓门状态',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remarks` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_gate_id` (`gate_id`),
  KEY `idx_gate_device_id` (`device_id`),
  KEY `idx_gate_device_sn` (`device_sn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓门信息表';

CREATE TABLE IF NOT EXISTS `order_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` VARCHAR(64) NOT NULL COMMENT '订单号',
  `customer_id` VARCHAR(64) NOT NULL COMMENT '客户ID',
  `device_id` VARCHAR(64) NOT NULL COMMENT '设备ID',
  `gate_id` VARCHAR(64) NOT NULL COMMENT '仓门ID',
  `order_amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '订单金额',
  `pay_amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '支付金额',
  `coupon_amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '优惠券抵扣金额',
  `trx_time` DATETIME DEFAULT NULL COMMENT '交易时间',
  `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `order_status` VARCHAR(32) NOT NULL COMMENT '订单状态',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remarks` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `procedure_type` VARCHAR(32) DEFAULT NULL COMMENT '小程序类型',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_order_customer_status` (`customer_id`, `order_status`),
  KEY `idx_order_device_id` (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单信息表';

CREATE TABLE IF NOT EXISTS `black_customer` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` VARCHAR(64) NOT NULL COMMENT '客户ID',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_black_customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='黑名单客户表';

CREATE TABLE IF NOT EXISTS `flash_sale` (
  `order_no` VARCHAR(64) NOT NULL COMMENT '订单号',
  `product_id` VARCHAR(64) NOT NULL COMMENT '商品ID',
  `sale_id` VARCHAR(64) NOT NULL COMMENT '活动ID',
  `user_id` VARCHAR(64) NOT NULL COMMENT '用户ID',
  `status` VARCHAR(32) NOT NULL COMMENT '状态',
  `price` DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '价格',
  PRIMARY KEY (`order_no`),
  UNIQUE KEY `uk_flash_sale_user` (`sale_id`, `user_id`),
  KEY `idx_flash_sale_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀订单表';

CREATE TABLE IF NOT EXISTS `coupon_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `coupon_config_id` VARCHAR(64) NOT NULL COMMENT '优惠券配置ID',
  `product_uid` VARCHAR(64) DEFAULT NULL COMMENT '商品ID',
  `coupon_type` VARCHAR(32) DEFAULT NULL COMMENT '优惠券类型',
  `coupon_money` DECIMAL(12,2) DEFAULT NULL COMMENT '优惠券金额',
  `coupon_all_categories` VARCHAR(8) DEFAULT NULL COMMENT '是否全品类',
  `coupon_count` BIGINT DEFAULT NULL COMMENT '优惠券数量',
  `coupon_valid_date` BIGINT DEFAULT NULL COMMENT '优惠券有效期天数',
  `coupon_image` VARCHAR(512) DEFAULT NULL COMMENT '优惠券图片',
  `coupon_name` VARCHAR(128) DEFAULT NULL COMMENT '优惠券名称',
  `coupon_receive_valid_date` BIGINT DEFAULT NULL COMMENT '领取有效期天数',
  `coupon_total_money` DECIMAL(12,2) DEFAULT NULL COMMENT '优惠券总金额',
  `coupon_customer_limit` BIGINT DEFAULT NULL COMMENT '总用户限制',
  `coupon_customer_amount_limit` BIGINT DEFAULT NULL COMMENT '单用户单次数量限制',
  `coupon_issued_count` BIGINT NOT NULL DEFAULT 0 COMMENT '已发放张数',
  `coupon_deadline` DATETIME DEFAULT NULL COMMENT '团购截止时间',
  `coupon_desc` VARCHAR(500) DEFAULT NULL COMMENT '券说明',
  `remarks` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_coupon_config_id` (`coupon_config_id`),
  KEY `idx_coupon_config_type` (`coupon_type`),
  KEY `idx_coupon_config_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券配置表';

SET @coupon_status_column_sql = IF(
  (SELECT COUNT(1) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'coupon_config' AND COLUMN_NAME = 'status') = 0,
  'ALTER TABLE `coupon_config` ADD COLUMN `status` VARCHAR(32) NOT NULL DEFAULT ''ACTIVE'' COMMENT ''状态'' AFTER `remarks`',
  'SELECT 1'
);
PREPARE coupon_status_column_stmt FROM @coupon_status_column_sql;
EXECUTE coupon_status_column_stmt;
DEALLOCATE PREPARE coupon_status_column_stmt;

SET @coupon_update_time_column_sql = IF(
  (SELECT COUNT(1) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'coupon_config' AND COLUMN_NAME = 'update_time') = 0,
  'ALTER TABLE `coupon_config` ADD COLUMN `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'' AFTER `create_time`',
  'SELECT 1'
);
PREPARE coupon_update_time_column_stmt FROM @coupon_update_time_column_sql;
EXECUTE coupon_update_time_column_stmt;
DEALLOCATE PREPARE coupon_update_time_column_stmt;

SET @coupon_status_index_sql = IF(
  (SELECT COUNT(1) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'coupon_config' AND INDEX_NAME = 'idx_coupon_config_status') = 0,
  'ALTER TABLE `coupon_config` ADD INDEX `idx_coupon_config_status` (`status`)',
  'SELECT 1'
);
PREPARE coupon_status_index_stmt FROM @coupon_status_index_sql;
EXECUTE coupon_status_index_stmt;
DEALLOCATE PREPARE coupon_status_index_stmt;

CREATE TABLE IF NOT EXISTS `device_event` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `event_id` VARCHAR(64) NOT NULL COMMENT '事件ID',
  `event_time` DATETIME NOT NULL COMMENT '事件时间',
  `event_type` VARCHAR(64) NOT NULL COMMENT '事件类型',
  `event_tag` VARCHAR(64) DEFAULT NULL COMMENT '事件标签',
  `device_id` VARCHAR(64) DEFAULT NULL COMMENT '设备ID',
  `device_sn` VARCHAR(64) DEFAULT NULL COMMENT '设备SN',
  `device_status` VARCHAR(32) DEFAULT NULL COMMENT '设备状态',
  `event_level` VARCHAR(32) NOT NULL DEFAULT 'INFO' COMMENT '事件级别',
  `content` VARCHAR(500) DEFAULT NULL COMMENT '事件内容',
  `order_no` VARCHAR(64) DEFAULT NULL COMMENT '关联订单号',
  `process_status` VARCHAR(32) NOT NULL DEFAULT 'CREATED' COMMENT '处理状态',
  `raw_payload` TEXT DEFAULT NULL COMMENT '原始消息',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_event_id` (`event_id`),
  KEY `idx_device_event_device_sn` (`device_sn`),
  KEY `idx_device_event_time` (`event_time`),
  KEY `idx_device_event_level` (`event_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备事件表';

CREATE TABLE IF NOT EXISTS `sys_admin_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` VARCHAR(64) NOT NULL COMMENT '管理员用户ID',
  `username` VARCHAR(64) NOT NULL COMMENT '登录账号',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '密码摘要',
  `real_name` VARCHAR(64) DEFAULT NULL COMMENT '真实姓名',
  `mobile` VARCHAR(32) DEFAULT NULL COMMENT '手机号',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_admin_user_id` (`user_id`),
  UNIQUE KEY `uk_sys_admin_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台管理员表';

CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` VARCHAR(64) NOT NULL COMMENT '角色ID',
  `role_name` VARCHAR(64) NOT NULL COMMENT '角色名称',
  `role_code` VARCHAR(64) NOT NULL COMMENT '角色编码',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '角色说明',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_id` (`role_id`),
  UNIQUE KEY `uk_sys_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台角色表';

CREATE TABLE IF NOT EXISTS `sys_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `permission_id` VARCHAR(64) NOT NULL COMMENT '权限ID',
  `permission_name` VARCHAR(64) NOT NULL COMMENT '权限名称',
  `permission_code` VARCHAR(128) NOT NULL COMMENT '权限编码',
  `permission_type` VARCHAR(32) NOT NULL COMMENT '权限类型',
  `menu_key` VARCHAR(64) DEFAULT NULL COMMENT '菜单分组',
  `parent_code` VARCHAR(128) DEFAULT NULL COMMENT '父级权限编码',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '权限说明',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_permission_id` (`permission_id`),
  UNIQUE KEY `uk_sys_permission_code` (`permission_code`),
  KEY `idx_sys_permission_menu_key` (`menu_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台权限点表';

CREATE TABLE IF NOT EXISTS `sys_role_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` VARCHAR(64) NOT NULL COMMENT '角色ID',
  `permission_id` VARCHAR(64) NOT NULL COMMENT '权限ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_permission` (`role_id`, `permission_id`),
  KEY `idx_sys_role_permission_permission` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关系表';

CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` VARCHAR(64) NOT NULL COMMENT '管理员用户ID',
  `role_id` VARCHAR(64) NOT NULL COMMENT '角色ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_role` (`user_id`, `role_id`),
  KEY `idx_sys_user_role_role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员角色关系表';

INSERT INTO `customer` (`customer_id`, `user_name`, `wechat_open_id`, `ali_open_id`, `mobile`, `status`, `remarks`)
VALUES
  ('CUST-10001', '张三', 'wx_8f1a1001', 'ali_7a2c1001', '13800010001', 'ACTIVE', '测试用户-正常'),
  ('CUST-10002', '李四', 'wx_8f1a1002', 'ali_7a2c1002', '13800010002', 'ACTIVE', '测试用户-正常'),
  ('CUST-10003', '王五', 'wx_8f1a1003', 'ali_7a2c1003', '13800010003', 'INACTIVE', '测试用户-停用'),
  ('CUST-10004', '赵六', 'wx_8f1a1004', 'ali_7a2c1004', '13800010004', 'ACTIVE', '测试用户-黑名单'),
  ('CUST-10005', '钱七', 'wx_8f1a1005', 'ali_7a2c1005', '13800010005', 'ACTIVE', '测试用户-无历史订单')
ON DUPLICATE KEY UPDATE
  `user_name` = VALUES(`user_name`),
  `wechat_open_id` = VALUES(`wechat_open_id`),
  `ali_open_id` = VALUES(`ali_open_id`),
  `mobile` = VALUES(`mobile`),
  `status` = VALUES(`status`),
  `remarks` = VALUES(`remarks`);

INSERT INTO `black_customer` (`customer_id`, `status`)
VALUES ('CUST-10004', 'ACTIVE')
ON DUPLICATE KEY UPDATE `status` = VALUES(`status`);

INSERT INTO `customer_agreement_sign` (`customer_id`, `status`, `order_no`, `agreement_no`, `sign_time`, `ali_open_id`)
VALUES
  ('CUST-10001', 'NORMAL', 'ORD-20260530-001', 'AGR-20260530-001', NOW(), 'ali_7a2c1001'),
  ('CUST-10002', 'NORMAL', 'ORD-20260530-002', 'AGR-20260530-002', NOW(), 'ali_7a2c1002')
ON DUPLICATE KEY UPDATE
  `status` = VALUES(`status`),
  `order_no` = VALUES(`order_no`),
  `sign_time` = VALUES(`sign_time`),
  `ali_open_id` = VALUES(`ali_open_id`);

INSERT INTO `device_info` (`device_id`, `device_sn`, `device_name`, `device_status`, `device_type`, `heating_switch_status`, `heating_threshold`, `close_heating_threshold`, `province`, `city`, `county`, `community`, `address`, `device_desc`, `lgt_on_time`, `addr_location_gd`, `addr_location_tx`, `device_version`, `remarks`)
VALUES
  ('DEV-001', 'SN-001', '虹桥站1号柜', 'ABNORMAL', 'SMART_CONTAINER', 'ON', 45, 55, '上海市', '上海市', '闵行区', '虹桥商圈', '虹桥站出入口A', '门锁异常测试设备', '08:00-22:00', '121.326,31.194', '121.326,31.194', 'v1.0.0', '测试设备-异常'),
  ('DEV-002', 'SN-002', '陆家嘴2号柜', 'NORMAL', 'SMART_CONTAINER', 'ON', 45, 55, '上海市', '上海市', '浦东新区', '陆家嘴', '陆家嘴中心B1', '正常在线测试设备', '08:00-22:00', '121.506,31.245', '121.506,31.245', 'v1.0.0', '测试设备-正常'),
  ('DEV-003', 'SN-003', '张江3号柜', 'OFF_LINE', 'SMART_CONTAINER', 'OFF', 45, 55, '上海市', '上海市', '浦东新区', '张江', '张江园区3号楼', '离线测试设备', '08:00-22:00', '121.610,31.205', '121.610,31.205', 'v1.0.0', '测试设备-离线'),
  ('DEV-004', 'SN-004', '五角场4号柜', 'NORMAL', 'SMART_CONTAINER', 'ON', 45, 55, '上海市', '上海市', '杨浦区', '五角场', '五角场商圈地铁口', '正常在线测试设备', '08:00-22:00', '121.522,31.303', '121.522,31.303', 'v1.0.1', '测试设备-正常')
ON DUPLICATE KEY UPDATE
  `device_sn` = VALUES(`device_sn`),
  `device_name` = VALUES(`device_name`),
  `device_status` = VALUES(`device_status`),
  `device_type` = VALUES(`device_type`),
  `heating_switch_status` = VALUES(`heating_switch_status`),
  `heating_threshold` = VALUES(`heating_threshold`),
  `close_heating_threshold` = VALUES(`close_heating_threshold`),
  `province` = VALUES(`province`),
  `city` = VALUES(`city`),
  `county` = VALUES(`county`),
  `community` = VALUES(`community`),
  `address` = VALUES(`address`),
  `device_desc` = VALUES(`device_desc`),
  `lgt_on_time` = VALUES(`lgt_on_time`),
  `addr_location_gd` = VALUES(`addr_location_gd`),
  `addr_location_tx` = VALUES(`addr_location_tx`),
  `device_version` = VALUES(`device_version`),
  `remarks` = VALUES(`remarks`);

INSERT INTO `gate_info` (`gate_id`, `device_id`, `device_sn`, `gate_name`, `gate_code`, `sort`, `gate_status`, `remarks`)
VALUES
  ('GATE-001-01', 'DEV-001', 'SN-001', '1号门', '01', 1, 'ABNORMAL', '异常门测试'),
  ('GATE-002-01', 'DEV-002', 'SN-002', '1号门', '01', 1, 'NORMAL', '正常门测试'),
  ('GATE-003-01', 'DEV-003', 'SN-003', '1号门', '01', 1, 'NORMAL', '离线设备门测试'),
  ('GATE-004-01', 'DEV-004', 'SN-004', '1号门', '01', 1, 'NORMAL', '正常门测试')
ON DUPLICATE KEY UPDATE
  `device_id` = VALUES(`device_id`),
  `device_sn` = VALUES(`device_sn`),
  `gate_name` = VALUES(`gate_name`),
  `gate_code` = VALUES(`gate_code`),
  `sort` = VALUES(`sort`),
  `gate_status` = VALUES(`gate_status`),
  `remarks` = VALUES(`remarks`);

INSERT INTO `order_info` (`order_no`, `customer_id`, `device_id`, `gate_id`, `order_amount`, `pay_amount`, `coupon_amount`, `trx_time`, `pay_time`, `order_status`, `remarks`, `procedure_type`)
VALUES
  ('ORD-20260530-001', 'CUST-10001', 'DEV-002', 'GATE-002-01', 12.00, 10.00, 2.00, NOW(), NOW(), 'FULLY_PAY', '测试已支付订单', 'ALI'),
  ('ORD-20260530-002', 'CUST-10002', 'DEV-002', 'GATE-002-01', 15.00, 15.00, 0.00, NOW(), NULL, 'PRE_AUTH_SUCCESS', '测试未支付订单', 'WECHAT'),
  ('ORD-20260530-003', 'CUST-10001', 'DEV-001', 'GATE-001-01', 20.00, 18.00, 2.00, NOW(), NULL, 'IN_PAYMENT', '测试支付中订单', 'ALI')
ON DUPLICATE KEY UPDATE
  `customer_id` = VALUES(`customer_id`),
  `device_id` = VALUES(`device_id`),
  `gate_id` = VALUES(`gate_id`),
  `order_amount` = VALUES(`order_amount`),
  `pay_amount` = VALUES(`pay_amount`),
  `coupon_amount` = VALUES(`coupon_amount`),
  `trx_time` = VALUES(`trx_time`),
  `pay_time` = VALUES(`pay_time`),
  `order_status` = VALUES(`order_status`),
  `remarks` = VALUES(`remarks`),
  `procedure_type` = VALUES(`procedure_type`);

INSERT INTO `verify_code` (`mobile`, `verify_code`, `order_no`, `retry_times`, `invalid_time`)
SELECT '13800010001', '123456', 'ORD-20260530-001', 0, DATE_ADD(NOW(), INTERVAL 10 MINUTE)
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `verify_code` WHERE `mobile` = '13800010001' AND `order_no` = 'ORD-20260530-001'
);

INSERT INTO `verify_code` (`mobile`, `verify_code`, `order_no`, `retry_times`, `invalid_time`)
SELECT '13800010002', '654321', 'ORD-20260530-002', 1, DATE_ADD(NOW(), INTERVAL 10 MINUTE)
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `verify_code` WHERE `mobile` = '13800010002' AND `order_no` = 'ORD-20260530-002'
);

INSERT INTO `flash_sale` (`order_no`, `product_id`, `sale_id`, `user_id`, `status`, `price`)
VALUES
  ('ORD-FS-20260530-001', 'PROD-001', 'SALE-001', 'CUST-10001', 'INIT', 9.90),
  ('ORD-FS-20260530-002', 'PROD-002', 'SALE-001', 'CUST-10002', 'PROCESSING', 19.90)
ON DUPLICATE KEY UPDATE
  `product_id` = VALUES(`product_id`),
  `sale_id` = VALUES(`sale_id`),
  `user_id` = VALUES(`user_id`),
  `status` = VALUES(`status`),
  `price` = VALUES(`price`);

INSERT INTO `coupon_config` (`coupon_config_id`, `product_uid`, `coupon_type`, `coupon_money`, `coupon_all_categories`, `coupon_count`, `coupon_valid_date`, `coupon_image`, `coupon_name`, `coupon_receive_valid_date`, `coupon_total_money`, `coupon_customer_limit`, `coupon_customer_amount_limit`, `coupon_issued_count`, `coupon_deadline`, `coupon_desc`, `remarks`, `status`)
VALUES
  ('CP-20260530-CASH-001', 'PROD-001', 'CASH', 2.00, 'Y', 100, 7, '', '测试现金券2元', 3, 200.00, 100, 1, 12, NULL, '测试现金券', '建表脚本测试数据', 'ACTIVE'),
  ('CP-20260530-GROUP-001', 'PROD-002', 'GROUP', 5.00, 'N', 50, 5, '', '测试团购券5元', 2, 250.00, 50, 1, 8, DATE_ADD(NOW(), INTERVAL 7 DAY), '测试团购券', '建表脚本测试数据', 'ACTIVE'),
  ('CP-20260530-CASH-002', 'PROD-003', 'CASH', 1.00, 'Y', 80, 5, '', '测试停用现金券1元', 2, 80.00, 80, 1, 20, NULL, '用于验证启用停用状态', '建表脚本测试数据', 'DISABLED')
ON DUPLICATE KEY UPDATE
  `product_uid` = VALUES(`product_uid`),
  `coupon_type` = VALUES(`coupon_type`),
  `coupon_money` = VALUES(`coupon_money`),
  `coupon_all_categories` = VALUES(`coupon_all_categories`),
  `coupon_count` = VALUES(`coupon_count`),
  `coupon_valid_date` = VALUES(`coupon_valid_date`),
  `coupon_image` = VALUES(`coupon_image`),
  `coupon_name` = VALUES(`coupon_name`),
  `coupon_receive_valid_date` = VALUES(`coupon_receive_valid_date`),
  `coupon_total_money` = VALUES(`coupon_total_money`),
  `coupon_customer_limit` = VALUES(`coupon_customer_limit`),
  `coupon_customer_amount_limit` = VALUES(`coupon_customer_amount_limit`),
  `coupon_issued_count` = VALUES(`coupon_issued_count`),
  `coupon_deadline` = VALUES(`coupon_deadline`),
  `coupon_desc` = VALUES(`coupon_desc`),
  `remarks` = VALUES(`remarks`),
  `status` = VALUES(`status`);

INSERT INTO `device_event` (`event_id`, `event_time`, `event_type`, `event_tag`, `device_id`, `device_sn`, `device_status`, `event_level`, `content`, `order_no`, `process_status`, `raw_payload`)
VALUES
  ('EVT-20260530-001', NOW() - INTERVAL 10 MINUTE, 'HEART_TIMEOUT', '告警', 'DEV-001', 'SN-001', 'ABNORMAL', 'WARNING', '设备 SN-001 心跳超时，需现场复核', NULL, 'CREATED', '{"source":"seed"}'),
  ('EVT-20260530-002', NOW() - INTERVAL 25 MINUTE, 'DEVICE_ONLINE', '上线', 'DEV-002', 'SN-002', 'NORMAL', 'INFO', '设备 SN-002 重新上线', NULL, 'DONE', '{"source":"seed"}'),
  ('EVT-20260530-003', NOW() - INTERVAL 1 HOUR, 'OPEN_GATE_FAIL', '告警', 'DEV-003', 'SN-003', 'OFF_LINE', 'WARNING', '设备 SN-003 开门失败，设备当前离线', NULL, 'CREATED', '{"source":"seed"}'),
  ('EVT-20260530-004', NOW() - INTERVAL 2 HOUR, 'FIRMWARE_UPGRADE', '信息', 'DEV-004', 'SN-004', 'NORMAL', 'INFO', '设备 SN-004 固件更新完成', NULL, 'DONE', '{"source":"seed"}'),
  ('EVT-20260530-005', NOW() - INTERVAL 30 MINUTE, 'ORDER_OPEN_GATE', '开门', 'DEV-002', 'SN-002', 'NORMAL', 'INFO', '订单 ORD-20260530-001 触发开门', 'ORD-20260530-001', 'DONE', '{"source":"seed"}'),
  ('EVT-20260530-006', NOW() - INTERVAL 3 MINUTE, 'HEART', '心跳', 'DEV-004', 'SN-004', 'NORMAL', 'INFO', '设备心跳正常，库存同步完成', NULL, 'DONE', '{"source":"seed"}')
ON DUPLICATE KEY UPDATE
  `event_time` = VALUES(`event_time`),
  `event_type` = VALUES(`event_type`),
  `event_tag` = VALUES(`event_tag`),
  `device_id` = VALUES(`device_id`),
  `device_sn` = VALUES(`device_sn`),
  `device_status` = VALUES(`device_status`),
  `event_level` = VALUES(`event_level`),
  `content` = VALUES(`content`),
  `order_no` = VALUES(`order_no`),
  `process_status` = VALUES(`process_status`),
  `raw_payload` = VALUES(`raw_payload`);

INSERT INTO `sys_admin_user` (`user_id`, `username`, `password_hash`, `real_name`, `mobile`, `status`)
VALUES
  ('ADM-001', 'admin', 'local-dev-password', '系统管理员', '13800019999', 'ACTIVE'),
  ('ADM-002', 'operator', 'local-dev-password', '运营人员', '13800018888', 'ACTIVE'),
  ('ADM-003', 'device_admin', 'local-dev-password', '设备管理员', '13800017777', 'ACTIVE')
ON DUPLICATE KEY UPDATE
  `password_hash` = VALUES(`password_hash`),
  `real_name` = VALUES(`real_name`),
  `mobile` = VALUES(`mobile`),
  `status` = VALUES(`status`);

INSERT INTO `sys_role` (`role_id`, `role_name`, `role_code`, `description`, `status`)
VALUES
  ('ROLE-001', '超级管理员', 'ADMIN', '拥有全部管理权限', 'ACTIVE'),
  ('ROLE-002', '运营人员', 'OPERATOR', '优惠券配置与运营管理', 'ACTIVE'),
  ('ROLE-003', '设备管理员', 'DEVICE_ADMIN', '设备监控与维护', 'ACTIVE')
ON DUPLICATE KEY UPDATE
  `role_name` = VALUES(`role_name`),
  `description` = VALUES(`description`),
  `status` = VALUES(`status`);

INSERT INTO `sys_permission` (`permission_id`, `permission_name`, `permission_code`, `permission_type`, `menu_key`, `parent_code`, `description`, `sort`, `status`)
VALUES
  ('PERM-001', '工作台查看', 'dashboard:view', 'MENU', 'dashboard', NULL, '查看工作台', 10, 'ACTIVE'),
  ('PERM-002', '优惠券配置新增', 'coupon:create', 'ACTION', 'coupon', NULL, '新增优惠券配置', 20, 'ACTIVE'),
  ('PERM-003', '优惠券配置列表', 'coupon:list', 'MENU', 'coupon', NULL, '查看优惠券配置列表', 30, 'ACTIVE'),
  ('PERM-004', '优惠券配置详情', 'coupon:detail', 'MENU', 'coupon', NULL, '查看优惠券配置详情', 40, 'ACTIVE'),
  ('PERM-005', '优惠券配置编辑', 'coupon:edit', 'ACTION', 'coupon', NULL, '编辑优惠券配置', 50, 'ACTIVE'),
  ('PERM-006', '优惠券配置停用', 'coupon:disable', 'ACTION', 'coupon', NULL, '停用优惠券配置', 60, 'ACTIVE'),
  ('PERM-007', '用户列表查看', 'user:list', 'MENU', 'user', NULL, '查看用户列表', 70, 'ACTIVE'),
  ('PERM-008', '用户详情查看', 'user:detail', 'MENU', 'user', NULL, '查看用户详情', 80, 'ACTIVE'),
  ('PERM-009', '用户状态变更', 'user:disable', 'ACTION', 'user', NULL, '启用、停用或拉黑用户', 90, 'ACTIVE'),
  ('PERM-010', '设备概览查看', 'device:overview', 'MENU', 'device', NULL, '查看设备概览', 100, 'ACTIVE'),
  ('PERM-011', '设备监控查看', 'device:monitor', 'MENU', 'device', NULL, '查看设备监控', 110, 'ACTIVE'),
  ('PERM-012', '远程开门操作', 'device:openGate', 'ACTION', 'device', NULL, '远程触发设备开门', 120, 'ACTIVE'),
  ('PERM-013', '设备重启操作', 'device:restart', 'ACTION', 'device', NULL, '远程触发设备重启', 130, 'ACTIVE'),
  ('PERM-014', '角色权限查看', 'system:role', 'MENU', 'system', NULL, '查看角色权限页面', 140, 'ACTIVE'),
  ('PERM-015', '权限配置查看', 'system:permission', 'MENU', 'system', NULL, '查看权限配置页面', 150, 'ACTIVE'),
  ('PERM-016', '订单列表查看', 'order:list', 'MENU', 'order', NULL, '查看订单列表', 160, 'ACTIVE'),
  ('PERM-017', '订单详情查看', 'order:detail', 'MENU', 'order', NULL, '查看订单详情', 170, 'ACTIVE'),
  ('PERM-018', '订单状态修正', 'order:changeStatus', 'ACTION', 'order', NULL, '标记支付、取消或异常订单', 180, 'ACTIVE')
ON DUPLICATE KEY UPDATE
  `permission_name` = VALUES(`permission_name`),
  `permission_type` = VALUES(`permission_type`),
  `menu_key` = VALUES(`menu_key`),
  `parent_code` = VALUES(`parent_code`),
  `description` = VALUES(`description`),
  `sort` = VALUES(`sort`),
  `status` = VALUES(`status`);

INSERT INTO `sys_user_role` (`user_id`, `role_id`)
VALUES
  ('ADM-001', 'ROLE-001'),
  ('ADM-002', 'ROLE-002'),
  ('ADM-003', 'ROLE-003')
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 'ROLE-001', `permission_id` FROM `sys_permission`;

INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 'ROLE-002', `permission_id` FROM `sys_permission`
WHERE `permission_code` IN ('dashboard:view', 'coupon:create', 'coupon:list', 'coupon:detail', 'coupon:edit', 'coupon:disable', 'user:list', 'user:detail', 'order:list', 'order:detail', 'order:changeStatus');

INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 'ROLE-003', `permission_id` FROM `sys_permission`
WHERE `permission_code` IN ('dashboard:view', 'device:overview', 'device:monitor', 'device:openGate', 'device:restart');
