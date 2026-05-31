package cn.fuguang.manager.service.impl;

import cn.fuguang.manager.mapper.AuditLogManageMapper;
import cn.fuguang.manager.service.AuditLogManageService;
import cn.fuguang.web.BaseResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AuditLogManageServiceImpl implements AuditLogManageService {

    @Resource
    private AuditLogManageMapper auditLogManageMapper;

    @Override
    public BaseResult<Map<String, Object>> logs(String keyword, String module, Integer pageNum, Integer pageSize) {
        int currentPage = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int currentSize = pageSize == null || pageSize < 1 ? 20 : pageSize;
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("records", auditLogManageMapper.selectLogs(trim(keyword), trim(module), (currentPage - 1) * currentSize, currentSize));
        data.put("total", auditLogManageMapper.countLogs(trim(keyword), trim(module)));
        data.put("pageNum", currentPage);
        data.put("pageSize", currentSize);
        return BaseResult.success(data);
    }

    @Override
    public BaseResult<Map<String, Object>> statistics() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("total", auditLogManageMapper.countAllLogs());
        data.put("todayTotal", auditLogManageMapper.countTodayLogs());
        data.put("moduleTotal", auditLogManageMapper.selectModuleTotals());
        return BaseResult.success(data);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
