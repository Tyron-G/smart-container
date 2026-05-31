package cn.fuguang.manager.mapper;

public interface DemoDataManageMapper {

    int ensureCustomers();

    int ensureBlackCustomers();

    int ensureAgreements();

    int ensureDevices();

    int ensureGates();

    int ensureOrders();

    int ensureOrderItems();

    int ensureOrderExceptions();

    int ensureCouponConfigs();

    int ensureCouponIssues();

    int ensureCouponUses();

    int ensureDeviceEvents();

    int ensureAdminUsers();

    int ensureRoles();

    int ensurePermissions();

    int ensureUserRoles();

    int ensureAdminRolePermissions();

    int ensureOperatorRolePermissions();

    int ensureDeviceRolePermissions();

    int ensureOperationLog();

    long countDemoCustomers();

    long countDemoDevices();

    long countDemoGates();

    long countDemoOrders();

    long countDemoOrderItems();

    long countDemoCouponConfigs();

    long countDemoCouponFlows();

    long countDemoDeviceEvents();

    long countDemoPermissions();

    long countDemoAuditLogs();
}
