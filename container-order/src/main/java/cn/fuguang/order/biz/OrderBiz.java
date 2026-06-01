package cn.fuguang.order.biz;

import cn.fuguang.api.order.dto.req.AgreementSignCallBackReqDTO;
import cn.fuguang.order.pojo.vo.req.ScanCreateOrderReq;
import cn.fuguang.order.pojo.vo.res.ScanCreateOrderRes;

import java.util.Map;

public interface OrderBiz {
    /**
     * 用户扫码下单
     */
    ScanCreateOrderRes scanCreateOrder(ScanCreateOrderReq req);

    /**
     * 接收channel服务预授权回调处理
     */
    void agreementSignCallBack(AgreementSignCallBackReqDTO reqDTO);

    Map<String, Object> queryCustomerOrders(String customerId, Integer pageNum, Integer pageSize);

    Map<String, Object> queryCustomerOrderDetail(String customerId, String orderNo);

    Map<String, Object> queryCustomerOrderStatus(String customerId, String orderNo);
}
