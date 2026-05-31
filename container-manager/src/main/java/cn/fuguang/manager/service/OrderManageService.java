package cn.fuguang.manager.service;

import cn.fuguang.manager.pojo.vo.req.OrderExceptionHandleReq;
import cn.fuguang.manager.pojo.vo.req.OrderPaymentReq;
import cn.fuguang.manager.pojo.vo.req.OrderStatusReq;
import cn.fuguang.web.BaseResult;

import java.util.Map;

public interface OrderManageService {

    BaseResult<Map<String, Object>> list(String keyword, String status, Integer pageNum, Integer pageSize);

    BaseResult<Map<String, Object>> detail(String orderNo);

    BaseResult<Map<String, Object>> statistics();

    BaseResult<Void> changeStatus(OrderStatusReq req);

    BaseResult<Map<String, Object>> refund(OrderPaymentReq req);

    BaseResult<Map<String, Object>> supplementCharge(OrderPaymentReq req);

    BaseResult<Void> handleException(OrderExceptionHandleReq req);
}
