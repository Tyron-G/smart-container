-- 2026-05-31: 管理端线上账号、角色与权限点初始化脚本，可重复执行。
USE `smart_container`;

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

INSERT INTO `sys_admin_user` (`user_id`, `username`, `password_hash`, `real_name`, `mobile`, `status`)
VALUES
  ('ADM-001', 'admin', 'local-dev-password', '系统管理员', '13800019999', 'ACTIVE'),
  ('ADM-002', 'operator', 'local-dev-password', '运营人员', '13800018888', 'ACTIVE'),
  ('ADM-003', 'device_admin', 'local-dev-password', '设备管理员', '13800017777', 'ACTIVE')
ON DUPLICATE KEY UPDATE
  `real_name` = VALUES(`real_name`),
  `mobile` = VALUES(`mobile`),
  `status` = VALUES(`status`);

INSERT INTO `sys_role` (`role_id`, `role_name`, `role_code`, `description`, `status`)
VALUES
  ('ROLE-001', '超级管理员', 'ADMIN', '拥有全部管理权限', 'ACTIVE'),
  ('ROLE-002', '运营人员', 'OPERATOR', '优惠券、用户与订单运营管理', 'ACTIVE'),
  ('ROLE-003', '设备管理员', 'DEVICE_ADMIN', '设备监控、仓门维护与事件处理', 'ACTIVE')
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
  ('PERM-006', '优惠券配置停用', 'coupon:disable', 'ACTION', 'coupon', NULL, '停用或删除优惠券配置', 60, 'ACTIVE'),
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
  ('PERM-018', '订单状态修正', 'order:changeStatus', 'ACTION', 'order', NULL, '标记支付、取消或异常订单', 180, 'ACTIVE'),
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

INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`, `create_time`)
SELECT 'ROLE-001', `permission_id`, NOW()
FROM `sys_permission`
WHERE `status` = 'ACTIVE';

INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`, `create_time`)
SELECT 'ROLE-002', `permission_id`, NOW()
FROM `sys_permission`
WHERE `permission_code` IN (
  'dashboard:view',
  'coupon:create', 'coupon:list', 'coupon:detail', 'coupon:edit', 'coupon:disable', 'coupon:flow', 'coupon:issue', 'coupon:revoke',
  'user:list', 'user:detail', 'user:disable', 'user:save', 'user:agreements', 'user:orders',
  'order:list', 'order:detail', 'order:changeStatus', 'order:refund', 'order:supplementCharge', 'order:handleException'
);

INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`, `create_time`)
SELECT 'ROLE-003', `permission_id`, NOW()
FROM `sys_permission`
WHERE `permission_code` IN (
  'dashboard:view',
  'device:overview', 'device:monitor', 'device:openGate', 'device:restart', 'device:save', 'device:saveGate', 'device:processEvent'
);
