package cn.fuguang.manager.controller;

import cn.fuguang.manager.pojo.vo.req.DeviceCommandReq;
import cn.fuguang.manager.pojo.vo.req.DeviceEventProcessReq;
import cn.fuguang.manager.pojo.vo.req.DeviceSaveReq;
import cn.fuguang.manager.pojo.vo.req.GateSaveReq;
import cn.fuguang.manager.service.DeviceManageService;
import cn.fuguang.web.BaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/manager/device")
@Slf4j
public class DeviceManageController {

    @Resource
    private DeviceManageService deviceManageService;

    @GetMapping("/list")
    public BaseResult<Map<String, Object>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                                @RequestParam(value = "status", required = false) String status,
                                                @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return deviceManageService.list(keyword, status, pageNum, pageSize);
    }

    @GetMapping("/detail")
    public BaseResult<Map<String, Object>> detail(@RequestParam("deviceId") String deviceId) {
        return deviceManageService.detail(deviceId);
    }

    @GetMapping("/onlineCount")
    public BaseResult<Map<String, Object>> onlineCount() {
        return deviceManageService.onlineCount();
    }

    @GetMapping("/events")
    public BaseResult<Map<String, Object>> events(@RequestParam(value = "keyword", required = false) String keyword,
                                                  @RequestParam(value = "status", required = false) String status,
                                                  @RequestParam(value = "processStatus", required = false) String processStatus,
                                                  @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                  @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        return deviceManageService.events(keyword, status, processStatus, pageNum, pageSize);
    }

    @PostMapping("/save")
    public BaseResult<Void> save(@RequestBody @Valid DeviceSaveReq req) {
        return deviceManageService.save(req);
    }

    @PostMapping("/saveGate")
    public BaseResult<Void> saveGate(@RequestBody @Valid GateSaveReq req) {
        return deviceManageService.saveGate(req);
    }

    @PostMapping("/markEventProcessed")
    public BaseResult<Void> markEventProcessed(@RequestBody @Valid DeviceEventProcessReq req) {
        return deviceManageService.markEventProcessed(req);
    }

    @PostMapping("/openGate")
    public BaseResult<Void> openGate(@RequestBody @Valid DeviceCommandReq req) {
        return deviceManageService.openGate(req);
    }

    @PostMapping("/restart")
    public BaseResult<Void> restart(@RequestBody @Valid DeviceCommandReq req) {
        return deviceManageService.restart(req);
    }
}
