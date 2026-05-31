package cn.fuguang.manager.service.impl;

import cn.fuguang.manager.mapper.DemoDataManageMapper;
import cn.fuguang.manager.service.DemoDataManageService;
import cn.fuguang.manager.service.ManagerLogService;
import cn.fuguang.web.BaseResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DemoDataManageServiceImpl implements DemoDataManageService {

    @Resource
    private DemoDataManageMapper demoDataManageMapper;

    @Resource
    private ManagerLogService managerLogService;

    @Override
    public BaseResult<Map<String, Object>> summary() {
        return BaseResult.success(buildSummary());
    }

    @Override
    public BaseResult<Map<String, Object>> ensure() {
        Map<String, Object> affected = new LinkedHashMap<String, Object>();
        affected.put("customers", demoDataManageMapper.ensureCustomers() + demoDataManageMapper.ensureBlackCustomers() + demoDataManageMapper.ensureAgreements());
        affected.put("devices", demoDataManageMapper.ensureDevices() + demoDataManageMapper.ensureGates() + demoDataManageMapper.ensureDeviceEvents());
        affected.put("orders", demoDataManageMapper.ensureOrders() + demoDataManageMapper.ensureOrderItems() + demoDataManageMapper.ensureOrderExceptions());
        affected.put("coupons", demoDataManageMapper.ensureCouponConfigs() + demoDataManageMapper.ensureCouponIssues() + demoDataManageMapper.ensureCouponUses());
        affected.put("permissions", demoDataManageMapper.ensureAdminUsers() + demoDataManageMapper.ensureRoles() + demoDataManageMapper.ensurePermissions()
                + demoDataManageMapper.ensureUserRoles() + demoDataManageMapper.ensureAdminRolePermissions()
                + demoDataManageMapper.ensureOperatorRolePermissions() + demoDataManageMapper.ensureDeviceRolePermissions());
        affected.put("auditLogs", demoDataManageMapper.ensureOperationLog());
        managerLogService.record("system", "DEMO_DATA_ENSURE", "smart-container", "admin", "2026-05-31: 幂等补齐管理端演示数据");

        Map<String, Object> data = buildSummary();
        data.put("affected", affected);
        return BaseResult.success(data);
    }

    private Map<String, Object> buildSummary() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("customers", metric(demoDataManageMapper.countDemoCustomers(), 5));
        data.put("devices", metric(demoDataManageMapper.countDemoDevices(), 4));
        data.put("gates", metric(demoDataManageMapper.countDemoGates(), 4));
        data.put("orders", metric(demoDataManageMapper.countDemoOrders(), 3));
        data.put("orderItems", metric(demoDataManageMapper.countDemoOrderItems(), 3));
        data.put("couponConfigs", metric(demoDataManageMapper.countDemoCouponConfigs(), 3));
        data.put("couponFlows", metric(demoDataManageMapper.countDemoCouponFlows(), 3));
        data.put("deviceEvents", metric(demoDataManageMapper.countDemoDeviceEvents(), 6));
        data.put("permissions", metric(demoDataManageMapper.countDemoPermissions(), 35));
        data.put("auditLogs", metric(demoDataManageMapper.countDemoAuditLogs(), 1));
        data.put("ready", ready(data));
        return data;
    }

    private Map<String, Object> metric(long actual, long expected) {
        Map<String, Object> metric = new LinkedHashMap<String, Object>();
        metric.put("actual", actual);
        metric.put("expected", expected);
        metric.put("done", actual >= expected);
        return metric;
    }

    @SuppressWarnings("unchecked")
    private boolean ready(Map<String, Object> data) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getValue() instanceof Map && !Boolean.TRUE.equals(((Map<String, Object>) entry.getValue()).get("done"))) {
                return false;
            }
        }
        return true;
    }
}
