package cn.fuguang.manager.service;

import cn.fuguang.web.BaseResult;

import java.util.Map;

public interface AuditLogManageService {

    BaseResult<Map<String, Object>> logs(String keyword, String module, Integer pageNum, Integer pageSize);

    BaseResult<Map<String, Object>> statistics();
}
