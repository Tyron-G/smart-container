package cn.fuguang.manager.service;

import cn.fuguang.manager.pojo.vo.req.CustomerSaveReq;
import cn.fuguang.manager.pojo.vo.req.CustomerStatusReq;
import cn.fuguang.web.BaseResult;

import java.util.List;
import java.util.Map;

public interface CustomerManageService {

    BaseResult<Map<String, Object>> list(String keyword, String status, Integer pageNum, Integer pageSize);

    BaseResult<Map<String, Object>> detail(String customerId);

    BaseResult<List<Map<String, Object>>> agreements(String customerId);

    BaseResult<Map<String, Object>> orders(String customerId, Integer pageNum, Integer pageSize);

    BaseResult<Map<String, Object>> statistics();

    BaseResult<Void> changeStatus(CustomerStatusReq req);

    BaseResult<Void> create(CustomerSaveReq req);

    BaseResult<Void> update(CustomerSaveReq req);
}
