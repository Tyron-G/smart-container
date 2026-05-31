package cn.fuguang.manager.service.impl;

import cn.fuguang.manager.mapper.DeviceManageMapper;
import cn.fuguang.manager.pojo.vo.req.DeviceCommandReq;
import cn.fuguang.manager.pojo.vo.req.DeviceEventProcessReq;
import cn.fuguang.manager.pojo.vo.req.DeviceSaveReq;
import cn.fuguang.manager.pojo.vo.req.GateSaveReq;
import cn.fuguang.manager.service.DeviceManageService;
import cn.fuguang.manager.service.ManagerLogService;
import cn.fuguang.web.BaseResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DeviceManageServiceImpl implements DeviceManageService {

    @Resource
    private DeviceManageMapper deviceManageMapper;

    @Resource
    private ManagerLogService managerLogService;

    @Override
    public BaseResult<Map<String, Object>> list(String keyword, String status, Integer pageNum, Integer pageSize) {
        int currentPage = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int currentSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        List<Map<String, Object>> records = deviceManageMapper.selectDevices(trim(keyword), toDbDeviceStatus(trim(status)), (currentPage - 1) * currentSize, currentSize);
        fillManagerStatus(records);
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("records", records);
        data.put("total", deviceManageMapper.countDevices(trim(keyword), toDbDeviceStatus(trim(status))));
        data.put("pageNum", currentPage);
        data.put("pageSize", currentSize);
        return BaseResult.success(data);
    }

    @Override
    public BaseResult<Map<String, Object>> detail(String deviceId) {
        Map<String, Object> device = deviceManageMapper.selectDeviceById(deviceId);
        if (device == null || device.isEmpty()) {
            return BaseResult.fail("设备不存在");
        }
        Map<String, Object> data = new LinkedHashMap<String, Object>(device);
        data.put("gates", deviceManageMapper.selectGatesByDeviceId(deviceId));
        data.put("managerStatus", toManagerDeviceStatus(String.valueOf(data.get("device_status"))));
        return BaseResult.success(data);
    }

    @Override
    public BaseResult<Map<String, Object>> onlineCount() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("total", deviceManageMapper.countAllDevices());
        data.put("onlineTotal", deviceManageMapper.countDevicesByStatus("NORMAL"));
        data.put("offlineTotal", deviceManageMapper.countDevicesByStatus("OFF_LINE"));
        data.put("errorTotal", deviceManageMapper.countErrorDevices());
        data.put("todayAlertTotal", deviceManageMapper.countTodayAlertEvents());
        return BaseResult.success(data);
    }

    @Override
    public BaseResult<Map<String, Object>> events(String keyword, String status, String processStatus, Integer pageNum, Integer pageSize) {
        int currentPage = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int currentSize = pageSize == null || pageSize < 1 ? 20 : pageSize;
        List<Map<String, Object>> records = deviceManageMapper.selectEvents(trim(keyword), toDbDeviceStatus(trim(status)), trim(processStatus), (currentPage - 1) * currentSize, currentSize);
        fillManagerStatus(records);
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("records", records);
        data.put("total", deviceManageMapper.countEvents(trim(keyword), toDbDeviceStatus(trim(status)), trim(processStatus)));
        data.put("pageNum", currentPage);
        data.put("pageSize", currentSize);
        return BaseResult.success(data);
    }

    @Override
    public BaseResult<Void> save(DeviceSaveReq req) {
        String deviceId = stringValue(req == null ? null : req.getDeviceId());
        String deviceSn = stringValue(req == null ? null : req.getDeviceSn());
        if (deviceId.length() == 0) {
            deviceId = "DEV-" + System.currentTimeMillis();
        }
        if (deviceSn.length() == 0) {
            return BaseResult.fail("deviceSn 不能为空");
        }
        deviceManageMapper.upsertDevice(deviceId, deviceSn, stringValue(req == null ? null : req.getDeviceName()),
                toDbDeviceStatus(defaultValue(req == null ? null : req.getDeviceStatus(), "ONLINE")),
                defaultValue(req == null ? null : req.getDeviceType(), "SMART_CONTAINER"),
                stringValue(req == null ? null : req.getProvince()), stringValue(req == null ? null : req.getCity()), stringValue(req == null ? null : req.getCounty()),
                stringValue(req == null ? null : req.getCommunity()), stringValue(req == null ? null : req.getAddress()), stringValue(req == null ? null : req.getRemarks()));
        managerLogService.record("device", "save", deviceId, stringValue(req == null ? null : req.getOperator()), "新增或编辑设备：" + deviceSn);
        return BaseResult.success();
    }

    @Override
    public BaseResult<Void> saveGate(GateSaveReq req) {
        String gateId = stringValue(req == null ? null : req.getGateId());
        String deviceId = stringValue(req == null ? null : req.getDeviceId());
        if (deviceId.length() == 0) {
            return BaseResult.fail("deviceId 不能为空");
        }
        Map<String, Object> device = deviceManageMapper.selectDeviceById(deviceId);
        if (device == null || device.isEmpty()) {
            return BaseResult.fail("设备不存在");
        }
        if (gateId.length() == 0) {
            gateId = "GATE-" + deviceId + "-" + System.currentTimeMillis();
        }
        deviceManageMapper.upsertGate(gateId, deviceId, String.valueOf(device.get("device_sn")), stringValue(req == null ? null : req.getGateName()),
                stringValue(req == null ? null : req.getGateCode()), intValue(req == null ? null : req.getSort()),
                toDbDeviceStatus(defaultValue(req == null ? null : req.getGateStatus(), "ONLINE")), stringValue(req == null ? null : req.getRemarks()));
        managerLogService.record("device", "saveGate", gateId, stringValue(req == null ? null : req.getOperator()), "维护仓门：" + gateId);
        return BaseResult.success();
    }

    @Override
    public BaseResult<Void> markEventProcessed(DeviceEventProcessReq req) {
        String id = stringValue(req == null ? null : req.getId());
        String operator = normalizeOperator(req == null ? null : req.getOperator());
        if (id.length() == 0) {
            return BaseResult.fail("id 不能为空");
        }
        int updated = deviceManageMapper.markEventProcessed(id, stringValue(req == null ? null : req.getRemark()), operator);
        if (updated == 0) {
            return BaseResult.fail("设备事件不存在");
        }
        managerLogService.record("device", "markEventProcessed", id, operator, "设备事件处理：" + stringValue(req == null ? null : req.getRemark()));
        return BaseResult.success();
    }

    @Override
    public BaseResult<Void> openGate(DeviceCommandReq req) {
        return insertDeviceCommand(req, "OPEN_GATE", "开门", "INFO",
                "管理员触发远程开门" + (stringValue(req == null ? null : req.getGateId()).length() == 0 ? "" : "，仓门：" + stringValue(req == null ? null : req.getGateId())));
    }

    @Override
    public BaseResult<Void> restart(DeviceCommandReq req) {
        return insertDeviceCommand(req, "RESTART_DEVICE", "重启", "SUCCESS", "管理员触发设备重启");
    }

    private BaseResult<Void> insertDeviceCommand(DeviceCommandReq req, String eventType, String eventTag, String eventLevel, String content) {
        String deviceId = stringValue(req == null ? null : req.getDeviceId());
        if (deviceId.length() == 0) {
            return BaseResult.fail("deviceId 不能为空");
        }
        Map<String, Object> device = deviceManageMapper.selectDeviceById(deviceId);
        if (device == null || device.isEmpty()) {
            return BaseResult.fail("设备不存在");
        }
        deviceManageMapper.insertDeviceCommandEvent(eventType, eventTag, eventLevel, deviceId,
                String.valueOf(device.get("device_sn")), String.valueOf(device.get("device_status")), content, String.valueOf(req));
        return BaseResult.success();
    }

    private void fillManagerStatus(List<Map<String, Object>> records) {
        for (Map<String, Object> record : records) {
            record.put("managerStatus", toManagerDeviceStatus(String.valueOf(record.get("device_status"))));
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private String defaultValue(Object value, String defaultValue) {
        String text = stringValue(value);
        return text.length() == 0 ? defaultValue : text;
    }

    private String normalizeOperator(Object value) {
        String operator = stringValue(value);
        return operator.length() == 0 ? "admin" : operator;
    }

    private Integer intValue(Object value) {
        try {
            return value == null || String.valueOf(value).trim().length() == 0 ? 0 : Integer.valueOf(String.valueOf(value).trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private String toDbDeviceStatus(String status) {
        if ("ONLINE".equals(status)) {
            return "NORMAL";
        }
        if ("OFFLINE".equals(status)) {
            return "OFF_LINE";
        }
        if ("ERROR".equals(status)) {
            return "ABNORMAL";
        }
        return status;
    }

    private String toManagerDeviceStatus(String status) {
        if ("NORMAL".equals(status)) {
            return "ONLINE";
        }
        if ("OFF_LINE".equals(status)) {
            return "OFFLINE";
        }
        return "ERROR";
    }
}
