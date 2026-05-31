package cn.fuguang.manager.controller;

import cn.fuguang.manager.service.DemoDataManageService;
import cn.fuguang.web.BaseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/manager/demo")
public class DemoDataManageController {

    @Resource
    private DemoDataManageService demoDataManageService;

    @GetMapping("/summary")
    public BaseResult<Map<String, Object>> summary() {
        return demoDataManageService.summary();
    }

    @PostMapping("/ensure")
    public BaseResult<Map<String, Object>> ensure() {
        return demoDataManageService.ensure();
    }
}
