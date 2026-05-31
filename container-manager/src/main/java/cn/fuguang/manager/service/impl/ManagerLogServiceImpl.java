package cn.fuguang.manager.service.impl;

import cn.fuguang.manager.mapper.ManagerLogMapper;
import cn.fuguang.manager.service.ManagerLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ManagerLogServiceImpl implements ManagerLogService {

    @Resource
    private ManagerLogMapper managerLogMapper;

    @Override
    public void record(String module, String action, String targetId, String operator, String content) {
        try {
            // 2026-05-31: 操作审计日志失败不能影响主业务提交。
            managerLogMapper.insertOperationLog(module, action, targetId, normalizeOperator(operator), content);
        } catch (Exception ignore) {
            // 2026-05-31: 管理端审计日志为旁路能力，失败时保留主流程可用性。
        }
    }

    private String normalizeOperator(String operator) {
        return operator == null || operator.trim().length() == 0 ? "admin" : operator.trim();
    }
}
