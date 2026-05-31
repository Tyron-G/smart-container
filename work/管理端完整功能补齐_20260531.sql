-- 2026-05-31: 管理端完整功能补齐表结构与测试数据
USE `smart_container`;

CREATE TABLE IF NOT EXISTS `order_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` VARCHAR(64) NOT NULL COMMENT '订单号',
  `product_id` VARCHAR(64) NOT NULL COMMENT '商品ID',
  `product_name` VARCHAR(128) NOT NULL COMMENT '商品名称',
  `sku_code` VARCHAR(64) DEFAULT NULL COMMENT 'SKU编码',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
  `unit_price` DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '单价',
  `total_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '小计金额',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_item_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品明细表';

CREATE TABLE IF NOT EXISTS `order_refund_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `refund_no` VARCHAR(64) NOT NULL COMMENT '退款单号',
  `order_no` VARCHAR(64) NOT NULL COMMENT '订单号',
  `refund_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '退款金额',
  `refund_reason` VARCHAR(255) DEFAULT NULL COMMENT '退款原因',
  `status` VARCHAR(32) NOT NULL DEFAULT 'PROCESSING' COMMENT '状态',
  `operator` VARCHAR(64) DEFAULT NULL COMMENT '操作人',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_refund_no` (`refund_no`),
  KEY `idx_refund_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单退款记录表';

CREATE TABLE IF NOT EXISTS `order_adjust_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `adjust_no` VARCHAR(64) NOT NULL COMMENT '补扣单号',
  `order_no` VARCHAR(64) NOT NULL COMMENT '订单号',
  `adjust_type` VARCHAR(32) NOT NULL COMMENT '调整类型',
  `adjust_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '调整金额',
  `adjust_reason` VARCHAR(255) DEFAULT NULL COMMENT '调整原因',
  `status` VARCHAR(32) NOT NULL DEFAULT 'PROCESSING' COMMENT '状态',
  `operator` VARCHAR(64) DEFAULT NULL COMMENT '操作人',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_adjust_no` (`adjust_no`),
  KEY `idx_adjust_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单补扣记录表';

CREATE TABLE IF NOT EXISTS `order_exception_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `exception_no` VARCHAR(64) NOT NULL COMMENT '异常单号',
  `order_no` VARCHAR(64) NOT NULL COMMENT '订单号',
  `exception_type` VARCHAR(64) NOT NULL COMMENT '异常类型',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '异常描述',
  `process_status` VARCHAR(32) NOT NULL DEFAULT 'CREATED' COMMENT '处理状态',
  `process_result` VARCHAR(500) DEFAULT NULL COMMENT '处理结果',
  `operator` VARCHAR(64) DEFAULT NULL COMMENT '操作人',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exception_no` (`exception_no`),
  KEY `idx_exception_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单异常处理记录表';

CREATE TABLE IF NOT EXISTS `coupon_issue_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `issue_no` VARCHAR(64) NOT NULL COMMENT '发放单号',
  `coupon_config_id` VARCHAR(64) NOT NULL COMMENT '优惠券配置ID',
  `customer_id` VARCHAR(64) NOT NULL COMMENT '客户ID',
  `coupon_name` VARCHAR(128) DEFAULT NULL COMMENT '优惠券名称',
  `coupon_amount` DECIMAL(12,2) DEFAULT NULL COMMENT '券金额',
  `status` VARCHAR(32) NOT NULL DEFAULT 'RECEIVED' COMMENT '状态',
  `receive_time` DATETIME DEFAULT NULL COMMENT '领取时间',
  `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_issue_no` (`issue_no`),
  KEY `idx_coupon_issue_config` (`coupon_config_id`),
  KEY `idx_coupon_issue_customer` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券发放领取记录表';

CREATE TABLE IF NOT EXISTS `coupon_use_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `use_no` VARCHAR(64) NOT NULL COMMENT '使用记录号',
  `issue_no` VARCHAR(64) NOT NULL COMMENT '发放单号',
  `coupon_config_id` VARCHAR(64) NOT NULL COMMENT '优惠券配置ID',
  `customer_id` VARCHAR(64) NOT NULL COMMENT '客户ID',
  `order_no` VARCHAR(64) NOT NULL COMMENT '订单号',
  `discount_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '抵扣金额',
  `status` VARCHAR(32) NOT NULL DEFAULT 'USED' COMMENT '状态',
  `use_time` DATETIME DEFAULT NULL COMMENT '使用时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_use_no` (`use_no`),
  KEY `idx_coupon_use_order` (`order_no`),
  KEY `idx_coupon_use_issue` (`issue_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券使用记录表';

CREATE TABLE IF NOT EXISTS `operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `log_id` VARCHAR(64) NOT NULL COMMENT '日志ID',
  `module` VARCHAR(64) NOT NULL COMMENT '业务模块',
  `action` VARCHAR(64) NOT NULL COMMENT '操作动作',
  `target_id` VARCHAR(128) DEFAULT NULL COMMENT '目标ID',
  `operator` VARCHAR(64) DEFAULT NULL COMMENT '操作人',
  `content` VARCHAR(1000) DEFAULT NULL COMMENT '操作内容',
  `result` VARCHAR(32) NOT NULL DEFAULT 'SUCCESS' COMMENT '操作结果',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_operation_log_id` (`log_id`),
  KEY `idx_operation_log_module` (`module`),
  KEY `idx_operation_log_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台操作日志表';

SET @device_event_process_remark_sql = IF(
  (SELECT COUNT(1) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'device_event' AND COLUMN_NAME = 'process_remark') = 0,
  'ALTER TABLE `device_event` ADD COLUMN `process_remark` VARCHAR(500) DEFAULT NULL COMMENT ''处理备注'' AFTER `process_status`',
  'SELECT 1'
);
PREPARE device_event_process_remark_stmt FROM @device_event_process_remark_sql;
EXECUTE device_event_process_remark_stmt;
DEALLOCATE PREPARE device_event_process_remark_stmt;

SET @device_event_processed_by_sql = IF(
  (SELECT COUNT(1) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'device_event' AND COLUMN_NAME = 'processed_by') = 0,
  'ALTER TABLE `device_event` ADD COLUMN `processed_by` VARCHAR(64) DEFAULT NULL COMMENT ''处理人'' AFTER `process_remark`',
  'SELECT 1'
);
PREPARE device_event_processed_by_stmt FROM @device_event_processed_by_sql;
EXECUTE device_event_processed_by_stmt;
DEALLOCATE PREPARE device_event_processed_by_stmt;

SET @device_event_processed_time_sql = IF(
  (SELECT COUNT(1) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'device_event' AND COLUMN_NAME = 'processed_time') = 0,
  'ALTER TABLE `device_event` ADD COLUMN `processed_time` DATETIME DEFAULT NULL COMMENT ''处理时间'' AFTER `processed_by`',
  'SELECT 1'
);
PREPARE device_event_processed_time_stmt FROM @device_event_processed_time_sql;
EXECUTE device_event_processed_time_stmt;
DEALLOCATE PREPARE device_event_processed_time_stmt;

INSERT INTO `order_item` (`order_no`, `product_id`, `product_name`, `sku_code`, `quantity`, `unit_price`, `total_amount`)
VALUES
  ('ORD-20260530-001', 'PROD-001', '冰美式咖啡', 'SKU-COFFEE-001', 1, 10.00, 10.00),
  ('ORD-20260530-002', 'PROD-002', '苏打气泡水', 'SKU-WATER-001', 2, 7.50, 15.00),
  ('ORD-20260530-003', 'PROD-003', '轻食三明治', 'SKU-FOOD-001', 1, 18.00, 18.00)
ON DUPLICATE KEY UPDATE `product_name` = VALUES(`product_name`);

INSERT INTO `order_exception_record` (`exception_no`, `order_no`, `exception_type`, `description`, `process_status`, `operator`)
VALUES
  ('EXC-20260531-001', 'ORD-20260530-003', 'PAY_TIMEOUT', '支付中订单长时间未完成，需要人工复核', 'CREATED', 'admin')
ON DUPLICATE KEY UPDATE `description` = VALUES(`description`), `process_status` = VALUES(`process_status`);

INSERT INTO `coupon_issue_record` (`issue_no`, `coupon_config_id`, `customer_id`, `coupon_name`, `coupon_amount`, `status`, `receive_time`, `expire_time`)
VALUES
  ('ISS-20260531-001', 'CP-20260530-CASH-001', 'CUST-10001', '测试现金券2元', 2.00, 'USED', NOW() - INTERVAL 1 DAY, NOW() + INTERVAL 6 DAY),
  ('ISS-20260531-002', 'CP-20260530-GROUP-001', 'CUST-10002', '测试团购券5元', 5.00, 'RECEIVED', NOW() - INTERVAL 2 HOUR, NOW() + INTERVAL 2 DAY)
ON DUPLICATE KEY UPDATE `status` = VALUES(`status`), `receive_time` = VALUES(`receive_time`);

INSERT INTO `coupon_use_record` (`use_no`, `issue_no`, `coupon_config_id`, `customer_id`, `order_no`, `discount_amount`, `status`, `use_time`)
VALUES
  ('USE-20260531-001', 'ISS-20260531-001', 'CP-20260530-CASH-001', 'CUST-10001', 'ORD-20260530-001', 2.00, 'USED', NOW() - INTERVAL 30 MINUTE)
ON DUPLICATE KEY UPDATE `discount_amount` = VALUES(`discount_amount`), `status` = VALUES(`status`);

INSERT INTO `operation_log` (`log_id`, `module`, `action`, `target_id`, `operator`, `content`, `result`)
VALUES
  ('LOG-20260531-001', 'system', 'seed', 'smart-container', 'system', '初始化管理端完整功能测试数据', 'SUCCESS')
ON DUPLICATE KEY UPDATE `content` = VALUES(`content`);
