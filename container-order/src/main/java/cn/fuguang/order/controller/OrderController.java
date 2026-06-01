package cn.fuguang.order.controller;

import cn.fuguang.order.biz.OrderBiz;
import cn.fuguang.order.pojo.vo.req.AliAuthReq;
import cn.fuguang.order.pojo.vo.req.ScanCreateOrderReq;
import cn.fuguang.order.pojo.vo.res.ScanCreateOrderRes;
import cn.fuguang.web.BaseResult;
import com.alibaba.fastjson2.JSONObject;
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
@RequestMapping("order")
@Slf4j
public class OrderController {

    @Resource
    private OrderBiz orderBiz;

    /**
     * 用户扫码下单
     */
    @PostMapping("/scanResultWithConfirm")
    public BaseResult<ScanCreateOrderRes> scanCreateOrder(@RequestBody @Valid ScanCreateOrderReq req){
        log.info("用户扫码下单 req:{}", JSONObject.toJSONString(req));
        ScanCreateOrderRes res;
        try {
            res = orderBiz.scanCreateOrder(req);
        } catch (Exception e) {
            log.error("用户扫码下单 系统异常" + e);
            return BaseResult.fail("系统异常, 请稍后重试");
        }
        log.info("用户扫码下单结果 res:{}", JSONObject.toJSONString(res));
        return BaseResult.success(res);
    }

    @GetMapping("/customer/list")
    public BaseResult<Map<String, Object>> queryCustomerOrders(@RequestParam("customerId") String customerId,
                                                               @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                               @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        if (customerId == null || customerId.trim().length() == 0) {
            return BaseResult.fail("customerId 不能为空");
        }
        try {
            return BaseResult.success(orderBiz.queryCustomerOrders(customerId.trim(), pageNum, pageSize));
        } catch (Exception e) {
            log.error("查询用户订单列表异常 customerId:{}", customerId, e);
            return BaseResult.fail("查询用户订单列表失败");
        }
    }

    @GetMapping("/customer/detail")
    public BaseResult<Map<String, Object>> queryCustomerOrderDetail(@RequestParam("customerId") String customerId,
                                                                    @RequestParam("orderNo") String orderNo) {
        if (customerId == null || customerId.trim().length() == 0 || orderNo == null || orderNo.trim().length() == 0) {
            return BaseResult.fail("customerId/orderNo 不能为空");
        }
        try {
            return BaseResult.success(orderBiz.queryCustomerOrderDetail(customerId.trim(), orderNo.trim()));
        } catch (Exception e) {
            log.error("查询用户订单详情异常 customerId:{}, orderNo:{}", customerId, orderNo, e);
            return BaseResult.fail("订单不存在");
        }
    }

}
