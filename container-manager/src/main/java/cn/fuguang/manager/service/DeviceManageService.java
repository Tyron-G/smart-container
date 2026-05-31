package cn.fuguang.manager.service;

import cn.fuguang.manager.pojo.vo.req.DeviceCommandReq;
import cn.fuguang.manager.pojo.vo.req.DeviceEventProcessReq;
import cn.fuguang.manager.pojo.vo.req.DeviceSaveReq;
import cn.fuguang.manager.pojo.vo.req.GateSaveReq;
import cn.fuguang.web.BaseResult;

import java.util.Map;

public interface DeviceManageService {

    BaseResult<Map<String, Object>> list(String keyword, String status, Integer pageNum, Integer pageSize);

    BaseResult<Map<String, Object>> detail(String deviceId);

    BaseResult<Map<String, Object>> onlineCount();

    BaseResult<Map<String, Object>> events(String keyword, String status, String processStatus, Integer pageNum, Integer pageSize);

    BaseResult<Void> save(DeviceSaveReq req);

    BaseResult<Void> saveGate(GateSaveReq req);

    BaseResult<Void> markEventProcessed(DeviceEventProcessReq req);

    BaseResult<Void> openGate(DeviceCommandReq req);

    BaseResult<Void> restart(DeviceCommandReq req);
}
