package cn.fuguang.manager.controller;

import cn.fuguang.manager.service.AuditLogManageService;
import cn.fuguang.web.BaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/manager/audit")
@Slf4j
public class AuditLogManageController {

    @Resource
    private AuditLogManageService auditLogManageService;

    @GetMapping("/logs")
    public BaseResult<Map<String, Object>> logs(@RequestParam(value = "keyword", required = false) String keyword,
                                                @RequestParam(value = "module", required = false) String module,
                                                @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        return auditLogManageService.logs(keyword, module, pageNum, pageSize);
    }

    @GetMapping("/statistics")
    public BaseResult<Map<String, Object>> statistics() {
        return auditLogManageService.statistics();
    }
}
