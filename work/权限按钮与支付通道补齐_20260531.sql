-- 2026-05-31: 管理端按钮级权限与支付通道操作记录补齐。
USE `smart_container`;

CREATE TABLE IF NOT EXISTS `payment_operation_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `request_no` VARCHAR(64) NOT NULL COMMENT '支付操作请求号',
  `order_no` VARCHAR(64) NOT NULL COMMENT '订单号',
  `operation_type` VARCHAR(32) NOT NULL COMMENT '操作类型：REFUND/SUPPLEMENT_CHARGE',
  `channel_type` VARCHAR(32) NOT NULL DEFAULT 'LOCAL_SIMULATED' COMMENT '支付通道',
  `amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '操作金额',
  `reason` VARCHAR(255) DEFAULT NULL COMMENT '原因',
  `status` VARCHAR(32) NOT NULL DEFAULT 'SUCCESS' COMMENT '状态',
  `channel_trade_no` VARCHAR(128) DEFAULT NULL COMMENT '通道流水号',
  `channel_message` TEXT DEFAULT NULL COMMENT '通道返回信息',
  `error_code` VARCHAR(64) DEFAULT NULL COMMENT '通道错误码',
  `channel_request_payload` TEXT DEFAULT NULL COMMENT '通道请求报文',
  `channel_response_payload` TEXT DEFAULT NULL COMMENT '通道响应报文',
  `operator` VARCHAR(64) DEFAULT NULL COMMENT '操作人',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_request_no` (`request_no`),
  KEY `idx_payment_order_no` (`order_no`),
  KEY `idx_payment_operation_type` (`operation_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付通道操作记录表';

DROP PROCEDURE IF EXISTS `add_column_if_missing_20260531`;
DELIMITER //
CREATE PROCEDURE `add_column_if_missing_20260531`(
  IN tableName VARCHAR(64),
  IN columnName VARCHAR(64),
  IN ddlSql TEXT
)
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = tableName
      AND COLUMN_NAME = columnName
  ) THEN
    SET @ddl = ddlSql;
    PREPARE stmt FROM @ddl;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;
END//
DELIMITER ;

CALL `add_column_if_missing_20260531`('order_refund_record', 'payment_request_no', 'ALTER TABLE `order_refund_record` ADD COLUMN `payment_request_no` VARCHAR(64) DEFAULT NULL COMMENT ''支付操作请求号''');
CALL `add_column_if_missing_20260531`('order_adjust_record', 'payment_request_no', 'ALTER TABLE `order_adjust_record` ADD COLUMN `payment_request_no` VARCHAR(64) DEFAULT NULL COMMENT ''支付操作请求号''');
CALL `add_column_if_missing_20260531`('payment_operation_record', 'error_code', 'ALTER TABLE `payment_operation_record` ADD COLUMN `error_code` VARCHAR(64) DEFAULT NULL COMMENT ''通道错误码'' AFTER `channel_message`');
CALL `add_column_if_missing_20260531`('payment_operation_record', 'channel_request_payload', 'ALTER TABLE `payment_operation_record` ADD COLUMN `channel_request_payload` TEXT DEFAULT NULL COMMENT ''通道请求报文'' AFTER `error_code`');
CALL `add_column_if_missing_20260531`('payment_operation_record', 'channel_response_payload', 'ALTER TABLE `payment_operation_record` ADD COLUMN `channel_response_payload` TEXT DEFAULT NULL COMMENT ''通道响应报文'' AFTER `channel_request_payload`');
ALTER TABLE `payment_operation_record` MODIFY COLUMN `channel_message` TEXT DEFAULT NULL COMMENT '通道返回信息';

DROP PROCEDURE IF EXISTS `add_column_if_missing_20260531`;

INSERT INTO `sys_permission` (`permission_id`, `permission_name`, `permission_code`, `permission_type`, `menu_key`, `parent_code`, `description`, `sort`, `status`)
VALUES
  ('PERM-019', '订单退款', 'order:refund', 'ACTION', 'order', NULL, '订单退款处理', 190, 'ACTIVE'),
  ('PERM-020', '订单补扣', 'order:supplementCharge', 'ACTION', 'order', NULL, '订单补扣处理', 200, 'ACTIVE'),
  ('PERM-021', '订单异常处理', 'order:handleException', 'ACTION', 'order', NULL, '处理订单异常记录', 210, 'ACTIVE'),
  ('PERM-022', '用户新增编辑', 'user:save', 'ACTION', 'user', NULL, '新增或编辑用户资料', 220, 'ACTIVE'),
  ('PERM-023', '用户签约记录', 'user:agreements', 'ACTION', 'user', NULL, '查看用户签约记录', 230, 'ACTIVE'),
  ('PERM-024', '用户订单历史', 'user:orders', 'ACTION', 'user', NULL, '查看用户订单历史', 240, 'ACTIVE'),
  ('PERM-025', '设备新增编辑', 'device:save', 'ACTION', 'device', NULL, '新增或编辑设备', 250, 'ACTIVE'),
  ('PERM-026', '仓门维护', 'device:saveGate', 'ACTION', 'device', NULL, '维护设备仓门', 260, 'ACTIVE'),
  ('PERM-027', '设备事件处理', 'device:processEvent', 'ACTION', 'device', NULL, '处理设备事件', 270, 'ACTIVE'),
  ('PERM-028', '角色保存', 'system:roleSave', 'ACTION', 'system', NULL, '新增或编辑角色', 280, 'ACTIVE'),
  ('PERM-029', '角色启停', 'system:roleStatus', 'ACTION', 'system', NULL, '启用或停用角色', 290, 'ACTIVE'),
  ('PERM-030', '权限保存', 'system:permissionSave', 'ACTION', 'system', NULL, '新增或编辑权限点', 300, 'ACTIVE'),
  ('PERM-031', '权限启停', 'system:permissionStatus', 'ACTION', 'system', NULL, '启用或停用权限点', 310, 'ACTIVE'),
  ('PERM-032', '优惠券流水', 'coupon:flow', 'MENU', 'coupon', NULL, '查看优惠券流水', 320, 'ACTIVE'),
  ('PERM-033', '优惠券发放', 'coupon:issue', 'ACTION', 'coupon', NULL, '手工发放优惠券', 330, 'ACTIVE'),
  ('PERM-034', '优惠券撤回', 'coupon:revoke', 'ACTION', 'coupon', NULL, '撤回未使用优惠券', 340, 'ACTIVE'),
  ('PERM-035', '审计日志', 'system:audit', 'MENU', 'system', NULL, '查看操作审计日志', 350, 'ACTIVE')
ON DUPLICATE KEY UPDATE
  `permission_name` = VALUES(`permission_name`),
  `permission_type` = VALUES(`permission_type`),
  `menu_key` = VALUES(`menu_key`),
  `description` = VALUES(`description`),
  `sort` = VALUES(`sort`),
  `status` = VALUES(`status`);

INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`, `create_time`)
SELECT 'ROLE-001', `permission_id`, NOW()
FROM `sys_permission`
WHERE `permission_code` IN (
  'order:refund', 'order:supplementCharge', 'order:handleException',
  'user:save', 'user:agreements', 'user:orders',
  'device:save', 'device:saveGate', 'device:processEvent',
  'system:roleSave', 'system:roleStatus', 'system:permissionSave', 'system:permissionStatus', 'system:audit',
  'coupon:flow', 'coupon:issue', 'coupon:revoke'
);
