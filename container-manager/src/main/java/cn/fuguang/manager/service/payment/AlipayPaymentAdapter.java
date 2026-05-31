package cn.fuguang.manager.service.payment;

import cn.fuguang.manager.config.ManagerPaymentProperties;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.RoundingMode;
import java.util.List;

@Component
public class AlipayPaymentAdapter implements PaymentChannelAdapter {

    @Resource
    private ManagerPaymentProperties managerPaymentProperties;

    @Override
    public String channelType() {
        return "ALIPAY";
    }

    @Override
    public PaymentChannelResult execute(PaymentChannelRequest request) {
        ManagerPaymentProperties.Alipay alipay = managerPaymentProperties.getAlipay();
        if (!alipay.isEnabled()) {
            // 2026-05-31: 默认关闭真实支付宝通道，未显式开启时只能记录待配置状态。
            return new PaymentChannelResult("PENDING_CONFIG", pendingTradeNo(request), "支付宝真实通道未开启，已记录请求但未触发资金划转", null, null, "ALIPAY_DISABLED");
        }
        List<String> missingFields = alipay.missingRequiredFields();
        if (!missingFields.isEmpty() || !alipay.validEnvironment()) {
            return new PaymentChannelResult("CONFIG_ERROR", pendingTradeNo(request), "支付宝真实通道配置不完整或环境不明确：" + missingFields, null, null, "ALIPAY_CONFIG_ERROR");
        }
        if (!"REFUND".equals(request.getOperationType())) {
            return new PaymentChannelResult("CONFIG_ERROR", pendingTradeNo(request), "支付宝真实补扣需协议扣款产品和签约号，当前未触发资金划转", null, null, "ALIPAY_OPERATION_UNSUPPORTED");
        }
        JSONObject bizContent = buildRefundBizContent(request, alipay);
        if (!bizContent.containsKey("trade_no") && !bizContent.containsKey("out_trade_no")) {
            return new PaymentChannelResult("CONFIG_ERROR", pendingTradeNo(request), "支付宝退款缺少原支付交易号；请传 originalChannelTradeNo，或确认后开启 allowOrderNoAsOutTradeNo", null, null, "ALIPAY_ORIGINAL_TRADE_NO_MISSING");
        }
        AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();
        String requestPayload = bizContent.toJSONString();
        alipayRequest.setBizContent(requestPayload);
        try {
            AlipayTradeRefundResponse response = buildClient(alipay).execute(alipayRequest);
            String channelTradeNo = nonBlank(response.getTradeNo()) ? response.getTradeNo() : response.getOutTradeNo();
            String responsePayload = response.getBody();
            if (response.isSuccess()) {
                return new PaymentChannelResult("SUCCESS", channelTradeNo, "支付宝退款成功：" + response.getMsg(), requestPayload, responsePayload, response.getSubCode());
            }
            return new PaymentChannelResult("FAILED", channelTradeNo, "支付宝退款失败：" + alipayMessage(firstNonBlank(response.getSubMsg(), response.getMsg())), requestPayload, responsePayload, response.getSubCode());
        } catch (AlipayApiException e) {
            return new PaymentChannelResult("FAILED", pendingTradeNo(request), "支付宝退款接口异常：" + alipayMessage(e.getMessage()), requestPayload, null, "ALIPAY_API_EXCEPTION");
        }
    }

    private JSONObject buildRefundBizContent(PaymentChannelRequest request, ManagerPaymentProperties.Alipay alipay) {
        JSONObject bizContent = new JSONObject();
        bizContent.put("refund_amount", request.getAmount().setScale(2, RoundingMode.HALF_UP).toPlainString());
        bizContent.put("out_request_no", request.getRequestNo());
        bizContent.put("refund_reason", request.getReason());
        if (nonBlank(request.getOriginalChannelTradeNo())) {
            bizContent.put("trade_no", request.getOriginalChannelTradeNo().trim());
        } else if (alipay.isAllowOrderNoAsOutTradeNo()) {
            bizContent.put("out_trade_no", request.getOrderNo());
        }
        return bizContent;
    }

    private AlipayClient buildClient(ManagerPaymentProperties.Alipay alipay) throws AlipayApiException {
        AlipayConfig config = new AlipayConfig();
        config.setServerUrl(alipay.getServerUrl());
        config.setAppId(alipay.getAppId());
        config.setPrivateKey(alipay.getMerchantPrivateKey());
        config.setAlipayPublicKey(alipay.getAlipayPublicKey());
        config.setSignType(alipay.getSignType());
        config.setCharset(alipay.getCharset());
        config.setFormat(alipay.getFormat());
        return new DefaultAlipayClient(config);
    }

    private String pendingTradeNo(PaymentChannelRequest request) {
        return "ALIPAY-PENDING-" + request.getRequestNo();
    }

    private boolean nonBlank(String value) {
        return value != null && value.trim().length() > 0;
    }

    private String firstNonBlank(String first, String second) {
        return nonBlank(first) ? first : second;
    }

    private String alipayMessage(String message) {
        String actual = message == null ? "" : message.trim();
        if (actual.contains("<!DOCTYPE") || actual.contains("<html") || actual.contains("&lt;!DOCTYPE") || actual.contains("&lt;html")) {
            return "支付宝沙箱网关返回 504 或非 JSON 响应，退款请求未确认成功；请稍后查询交易或重新发起退款验证。";
        }
        return actual.length() == 0 ? "支付宝网关未返回明确错误信息" : actual;
    }
}
