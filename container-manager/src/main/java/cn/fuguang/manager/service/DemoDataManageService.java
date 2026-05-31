package cn.fuguang.manager.service;

import cn.fuguang.web.BaseResult;

import java.util.Map;

public interface DemoDataManageService {

    BaseResult<Map<String, Object>> summary();

    BaseResult<Map<String, Object>> ensure();
}
