package cn.fuguang.manager.controller;

import cn.fuguang.manager.pojo.vo.req.OrderExceptionHandleReq;
import cn.fuguang.manager.pojo.vo.req.OrderPaymentReq;
import cn.fuguang.manager.pojo.vo.req.OrderStatusReq;
import cn.fuguang.manager.service.OrderManageService;
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
@RequestMapping("/api/manager/order")
@Slf4j
public class OrderManageController {

    @Resource
    private OrderManageService orderManageService;

    @GetMapping("/list")
    public BaseResult<Map<String, Object>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                                @RequestParam(value = "status", required = false) String status,
                                                @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return orderManageService.list(keyword, status, pageNum, pageSize);
    }

    @GetMapping("/detail")
    public BaseResult<Map<String, Object>> detail(@RequestParam("orderNo") String orderNo) {
        return orderManageService.detail(orderNo);
    }

    @GetMapping("/statistics")
    public BaseResult<Map<String, Object>> statistics() {
        return orderManageService.statistics();
    }

    @PostMapping("/changeStatus")
    public BaseResult<Void> changeStatus(@RequestBody @Valid OrderStatusReq req) {
        return orderManageService.changeStatus(req);
    }

    @PostMapping("/refund")
    public BaseResult<Map<String, Object>> refund(@RequestBody @Valid OrderPaymentReq req) {
        return orderManageService.refund(req);
    }

    @PostMapping("/supplementCharge")
    public BaseResult<Map<String, Object>> supplementCharge(@RequestBody @Valid OrderPaymentReq req) {
        return orderManageService.supplementCharge(req);
    }

    @PostMapping("/handleException")
    public BaseResult<Void> handleException(@RequestBody @Valid OrderExceptionHandleReq req) {
        return orderManageService.handleException(req);
    }
}
