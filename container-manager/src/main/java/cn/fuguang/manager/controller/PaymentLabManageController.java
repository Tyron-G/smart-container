package cn.fuguang.manager.controller;

import cn.fuguang.manager.pojo.vo.req.AlipaySandboxPrecreateReq;
import cn.fuguang.manager.pojo.vo.req.AlipaySandboxRefundReq;
import cn.fuguang.manager.service.AlipaySandboxLabService;
import cn.fuguang.web.BaseResult;
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
@RequestMapping("/api/manager/paymentLab")
public class PaymentLabManageController {

    @Resource
    private AlipaySandboxLabService alipaySandboxLabService;

    @PostMapping("/alipay/precreate")
    public BaseResult<Map<String, Object>> alipayPrecreate(@RequestBody @Valid AlipaySandboxPrecreateReq req) {
        return alipaySandboxLabService.precreate(req);
    }

    @GetMapping("/alipay/query")
    public BaseResult<Map<String, Object>> alipayQuery(@RequestParam("outTradeNo") String outTradeNo) {
        return alipaySandboxLabService.query(outTradeNo);
    }

    @PostMapping("/alipay/refund")
    public BaseResult<Map<String, Object>> alipayRefund(@RequestBody @Valid AlipaySandboxRefundReq req) {
        return alipaySandboxLabService.refund(req);
    }
}
