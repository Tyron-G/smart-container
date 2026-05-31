package cn.fuguang.manager.service.impl;

import cn.fuguang.manager.mapper.CustomerManageMapper;
import cn.fuguang.manager.pojo.vo.req.CustomerSaveReq;
import cn.fuguang.manager.pojo.vo.req.CustomerStatusReq;
import cn.fuguang.manager.service.CustomerManageService;
import cn.fuguang.manager.service.ManagerLogService;
import cn.fuguang.web.BaseResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomerManageServiceImpl implements CustomerManageService {

    @Resource
    private CustomerManageMapper customerManageMapper;

    @Resource
    private ManagerLogService managerLogService;

    @Override
    public BaseResult<Map<String, Object>> list(String keyword, String status, Integer pageNum, Integer pageSize) {
        int currentPage = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int currentSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("records", customerManageMapper.selectCustomers(trim(keyword), trim(status), (currentPage - 1) * currentSize, currentSize));
        data.put("total", customerManageMapper.countCustomers(trim(keyword), trim(status)));
        data.put("pageNum", currentPage);
        data.put("pageSize", currentSize);
        return BaseResult.success(data);
    }

    @Override
    public BaseResult<Map<String, Object>> detail(String customerId) {
        Map<String, Object> customer = customerManageMapper.selectCustomerDetail(customerId);
        if (customer == null || customer.isEmpty()) {
            return BaseResult.fail("客户不存在");
        }
        Map<String, Object> data = new LinkedHashMap<String, Object>(customer);
        data.put("agreements", customerManageMapper.selectAgreements(customerId));
        data.put("recentOrders", customerManageMapper.selectRecentOrders(customerId));
        return BaseResult.success(data);
    }

    @Override
    public BaseResult<List<Map<String, Object>>> agreements(String customerId) {
        return BaseResult.success(customerManageMapper.selectAgreements(customerId));
    }

    @Override
    public BaseResult<Map<String, Object>> orders(String customerId, Integer pageNum, Integer pageSize) {
        int currentPage = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int currentSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("records", customerManageMapper.selectCustomerOrders(customerId, (currentPage - 1) * currentSize, currentSize));
        data.put("total", customerManageMapper.countCustomerOrders(customerId));
        data.put("pageNum", currentPage);
        data.put("pageSize", currentSize);
        return BaseResult.success(data);
    }

    @Override
    public BaseResult<Map<String, Object>> statistics() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("total", customerManageMapper.countAllCustomers());
        data.put("activeTotal", customerManageMapper.countActiveCustomers());
        data.put("disabledTotal", customerManageMapper.countDisabledCustomers());
        data.put("blacklistTotal", customerManageMapper.countBlacklistCustomers());
        return BaseResult.success(data);
    }

    @Override
    public BaseResult<Void> changeStatus(CustomerStatusReq req) {
        String customerId = stringValue(req == null ? null : req.getCustomerId());
        String status = stringValue(req == null ? null : req.getStatus());
        if (customerId.length() == 0 || status.length() == 0) {
            return BaseResult.fail("customerId/status 不能为空");
        }
        if ("BLACKLIST".equals(status)) {
            customerManageMapper.updateCustomerStatus(customerId, "ACTIVE");
            customerManageMapper.upsertBlackCustomer(customerId, "ACTIVE");
        } else {
            customerManageMapper.upsertBlackCustomer(customerId, "INACTIVE");
            customerManageMapper.updateCustomerStatus(customerId, "ACTIVE".equals(status) ? "ACTIVE" : "INACTIVE");
        }
        managerLogService.record("customer", "changeStatus", customerId, stringValue(req == null ? null : req.getOperator()), "用户状态变更为：" + status);
        return BaseResult.success();
    }

    @Override
    public BaseResult<Void> create(CustomerSaveReq req) {
        String customerId = stringValue(req == null ? null : req.getCustomerId());
        String mobile = stringValue(req == null ? null : req.getMobile());
        if (customerId.length() == 0) {
            customerId = "CUST-" + System.currentTimeMillis();
        }
        if (mobile.length() == 0) {
            return BaseResult.fail("mobile 不能为空");
        }
        customerManageMapper.upsertCustomer(customerId, stringValue(req == null ? null : req.getUserName()), mobile,
                stringValue(req == null ? null : req.getAliOpenId()), stringValue(req == null ? null : req.getWechatOpenId()), stringValue(req == null ? null : req.getRemarks()));
        managerLogService.record("customer", "create", customerId, stringValue(req == null ? null : req.getOperator()), "新增用户：" + mobile);
        return BaseResult.success();
    }

    @Override
    public BaseResult<Void> update(CustomerSaveReq req) {
        String customerId = stringValue(req == null ? null : req.getCustomerId());
        if (customerId.length() == 0) {
            return BaseResult.fail("customerId 不能为空");
        }
        int updated = customerManageMapper.updateCustomer(customerId, stringValue(req == null ? null : req.getUserName()), stringValue(req == null ? null : req.getMobile()),
                stringValue(req == null ? null : req.getAliOpenId()), stringValue(req == null ? null : req.getWechatOpenId()), stringValue(req == null ? null : req.getRemarks()));
        if (updated == 0) {
            return BaseResult.fail("客户不存在");
        }
        managerLogService.record("customer", "update", customerId, stringValue(req == null ? null : req.getOperator()), "编辑用户资料");
        return BaseResult.success();
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }
}
