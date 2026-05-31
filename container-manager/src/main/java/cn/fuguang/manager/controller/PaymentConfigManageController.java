package cn.fuguang.manager.controller;

import cn.fuguang.manager.config.ManagerPaymentProperties;
import cn.fuguang.web.BaseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/manager/payment")
public class PaymentConfigManageController {

    @Resource
    private ManagerPaymentProperties managerPaymentProperties;

    @GetMapping("/configStatus")
    public BaseResult<Map<String, Object>> configStatus() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("alipay", alipayStatus());
        data.put("wechat", wechatStatus());
        return BaseResult.success(data);
    }

    private Map<String, Object> alipayStatus() {
        ManagerPaymentProperties.Alipay alipay = managerPaymentProperties.getAlipay();
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("enabled", alipay.isEnabled());
        data.put("environment", safeEnvironment(alipay.getEnvironment()));
        data.put("validEnvironment", alipay.validEnvironment());
        data.put("missingFields", alipay.missingRequiredFields());
        data.put("readyForRealRefund", alipay.readyForRealCall());
        data.put("allowOrderNoAsOutTradeNo", alipay.isAllowOrderNoAsOutTradeNo());
        return data;
    }

    private Map<String, Object> wechatStatus() {
        ManagerPaymentProperties.Wechat wechat = managerPaymentProperties.getWechat();
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("enabled", wechat.isEnabled());
        data.put("environment", safeEnvironment(wechat.getEnvironment()));
        data.put("validEnvironment", wechat.validEnvironment());
        data.put("missingFields", wechat.missingRequiredFields());
        data.put("privateKeyPathConfigured", hasText(wechat.getPrivateKeyPath()));
        data.put("privateKeyFileExists", privateKeyFileExists(wechat.getPrivateKeyPath()));
        data.put("refundNotifyUrlConfigured", hasText(wechat.getRefundNotifyUrl()));
        data.put("readyForRealRefund", wechat.readyForRealCall() && privateKeyFileExists(wechat.getPrivateKeyPath()));
        data.put("localTestMode", wechat.isLocalTestMode());
        data.put("readyForLocalWechatRefund", wechat.isEnabled() && wechat.isLocalTestMode());
        data.put("allowOrderNoAsOutTradeNo", wechat.isAllowOrderNoAsOutTradeNo());
        return data;
    }

    private boolean privateKeyFileExists(String path) {
        return hasText(path) && new File(path).isFile();
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    private String safeEnvironment(String environment) {
        return hasText(environment) ? environment.trim() : "";
    }
}
