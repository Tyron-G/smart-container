-- 2026-05-31: 真实支付通道接入审计字段补齐，只扩展本地支付流水表，不写入任何商户密钥。
USE `smart_container`;

DROP PROCEDURE IF EXISTS `add_payment_column_if_missing_20260531`;
DELIMITER //
CREATE PROCEDURE `add_payment_column_if_missing_20260531`(
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

CALL `add_payment_column_if_missing_20260531`('payment_operation_record', 'error_code', 'ALTER TABLE `payment_operation_record` ADD COLUMN `error_code` VARCHAR(64) DEFAULT NULL COMMENT ''通道错误码'' AFTER `channel_message`');
CALL `add_payment_column_if_missing_20260531`('payment_operation_record', 'channel_request_payload', 'ALTER TABLE `payment_operation_record` ADD COLUMN `channel_request_payload` TEXT DEFAULT NULL COMMENT ''通道请求报文'' AFTER `error_code`');
CALL `add_payment_column_if_missing_20260531`('payment_operation_record', 'channel_response_payload', 'ALTER TABLE `payment_operation_record` ADD COLUMN `channel_response_payload` TEXT DEFAULT NULL COMMENT ''通道响应报文'' AFTER `channel_request_payload`');

ALTER TABLE `payment_operation_record`
  MODIFY COLUMN `channel_message` TEXT DEFAULT NULL COMMENT '通道返回信息';

DROP PROCEDURE IF EXISTS `add_payment_column_if_missing_20260531`;
