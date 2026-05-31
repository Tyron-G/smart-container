package cn.fuguang.manager.service.payment;

import cn.fuguang.manager.config.ManagerPaymentProperties;
import com.alibaba.fastjson2.JSONObject;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.exception.HttpException;
import com.wechat.pay.java.core.exception.MalformedMessageException;
import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.core.exception.ValidationException;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.AmountReq;
import com.wechat.pay.java.service.refund.model.CreateRequest;
import com.wechat.pay.java.service.refund.model.Refund;
import com.wechat.pay.java.service.refund.model.Status;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class WechatPaymentAdapter implements PaymentChannelAdapter {

    @Resource
    private ManagerPaymentProperties managerPaymentProperties;

    @Override
    public String channelType() {
        return "WECHAT";
    }

    @Override
    public PaymentChannelResult execute(PaymentChannelRequest request) {
        ManagerPaymentProperties.Wechat wechat = managerPaymentProperties.getWechat();
        if (!wechat.isEnabled()) {
            // 2026-05-31: 默认关闭真实微信通道，避免在无证书和商户配置时误触发资金请求。
            return new PaymentChannelResult("PENDING_CONFIG", "WECHAT-PENDING-" + request.getRequestNo(), "微信真实通道未开启，已记录请求但未触发资金划转", null, null, "WECHAT_DISABLED");
        }
        if (wechat.isLocalTestMode()) {
            return executeLocalTest(request);
        }
        List<String> missingFields = wechat.missingRequiredFields();
        if (!missingFields.isEmpty() || !wechat.validEnvironment()) {
            return new PaymentChannelResult("CONFIG_ERROR", pendingTradeNo(request), "微信真实通道配置不完整或环境不明确：" + missingFields, null, null, "WECHAT_CONFIG_ERROR");
        }
        if (!"REFUND".equals(request.getOperationType())) {
            return new PaymentChannelResult("CONFIG_ERROR", pendingTradeNo(request), "微信真实补扣需委托扣款协议和扣款接口，当前未触发资金划转", null, null, "WECHAT_OPERATION_UNSUPPORTED");
        }
        if (request.getOriginalOrderAmount() == null || request.getOriginalOrderAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return new PaymentChannelResult("CONFIG_ERROR", pendingTradeNo(request), "微信退款缺少原订单支付金额，当前未触发资金划转", null, null, "WECHAT_ORIGINAL_AMOUNT_MISSING");
        }
        CreateRequest wechatRequest = buildRefundRequest(request, wechat);
        if (!nonBlank(wechatRequest.getTransactionId()) && !nonBlank(wechatRequest.getOutTradeNo())) {
            return new PaymentChannelResult("CONFIG_ERROR", pendingTradeNo(request), "微信退款缺少原支付交易号；请传 originalChannelTradeNo，或确认后开启 allowOrderNoAsOutTradeNo", null, null, "WECHAT_ORIGINAL_TRADE_NO_MISSING");
        }
        String requestPayload = JSONObject.toJSONString(wechatRequest);
        try {
            Refund refund = buildService(wechat).create(wechatRequest);
            String responsePayload = JSONObject.toJSONString(refund);
            String status = mapRefundStatus(refund.getStatus());
            String channelTradeNo = nonBlank(refund.getRefundId()) ? refund.getRefundId() : refund.getOutRefundNo();
            return new PaymentChannelResult(status, channelTradeNo, "微信退款状态：" + refund.getStatus(), requestPayload, responsePayload, status.equals("SUCCESS") ? null : "WECHAT_REFUND_" + refund.getStatus());
        } catch (ServiceException e) {
            return new PaymentChannelResult("FAILED", pendingTradeNo(request), "微信退款失败：" + e.getErrorMessage(), requestPayload, e.getResponseBody(), e.getErrorCode());
        } catch (HttpException e) {
            return new PaymentChannelResult("FAILED", pendingTradeNo(request), "微信退款 HTTP 异常：" + e.getMessage(), requestPayload, null, "WECHAT_HTTP_EXCEPTION");
        } catch (ValidationException e) {
            return new PaymentChannelResult("FAILED", pendingTradeNo(request), "微信退款验签异常：" + e.getMessage(), requestPayload, null, "WECHAT_VALIDATION_EXCEPTION");
        } catch (MalformedMessageException e) {
            return new PaymentChannelResult("FAILED", pendingTradeNo(request), "微信退款响应解析异常：" + e.getMessage(), requestPayload, null, "WECHAT_MALFORMED_MESSAGE");
        } catch (RuntimeException e) {
            return new PaymentChannelResult("FAILED", pendingTradeNo(request), "微信退款接口异常：" + e.getMessage(), requestPayload, null, "WECHAT_RUNTIME_EXCEPTION");
        }
    }

    private PaymentChannelResult executeLocalTest(PaymentChannelRequest request) {
        if (!"REFUND".equals(request.getOperationType())) {
            return new PaymentChannelResult("CONFIG_ERROR", pendingTradeNo(request), "微信本地联调模式仅验证退款链路，补扣仍需委托扣款产品", null, null, "WECHAT_OPERATION_UNSUPPORTED");
        }
        if (request.getOriginalOrderAmount() == null || request.getOriginalOrderAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return new PaymentChannelResult("CONFIG_ERROR", pendingTradeNo(request), "微信本地联调退款缺少原订单支付金额", null, null, "WECHAT_ORIGINAL_AMOUNT_MISSING");
        }
        JSONObject requestPayload = new JSONObject();
        requestPayload.put("out_refund_no", request.getRequestNo());
        requestPayload.put("order_no", request.getOrderNo());
        requestPayload.put("refund_amount", toCent(request.getAmount()));
        requestPayload.put("total_amount", toCent(request.getOriginalOrderAmount()));
        requestPayload.put("currency", "CNY");
        requestPayload.put("local_test_mode", true);

        JSONObject responsePayload = new JSONObject();
        responsePayload.put("refund_id", "WECHAT-LOCAL-REFUND-" + request.getRequestNo());
        responsePayload.put("status", "SUCCESS");
        responsePayload.put("message", "local wechat refund verified without external fund transfer");
        responsePayload.put("local_test_mode", true);

        // 2026-05-31: 微信本地联调模式只验证管理端业务闭环和审计落库，不调用微信官方资金接口。
        return new PaymentChannelResult("SUCCESS", responsePayload.getString("refund_id"),
                "微信本地联调退款成功，未触发微信真实资金划转",
                requestPayload.toJSONString(), responsePayload.toJSONString(), null);
    }

    private CreateRequest buildRefundRequest(PaymentChannelRequest request, ManagerPaymentProperties.Wechat wechat) {
        CreateRequest wechatRequest = new CreateRequest();
        wechatRequest.setOutRefundNo(request.getRequestNo());
        wechatRequest.setReason(request.getReason());
        if (nonBlank(wechat.getRefundNotifyUrl())) {
            wechatRequest.setNotifyUrl(wechat.getRefundNotifyUrl().trim());
        }
        if (nonBlank(request.getOriginalChannelTradeNo())) {
            wechatRequest.setTransactionId(request.getOriginalChannelTradeNo().trim());
        } else if (wechat.isAllowOrderNoAsOutTradeNo()) {
            wechatRequest.setOutTradeNo(request.getOrderNo());
        }
        AmountReq amount = new AmountReq();
        amount.setRefund(toCent(request.getAmount()));
        amount.setTotal(toCent(request.getOriginalOrderAmount()));
        amount.setCurrency("CNY");
        wechatRequest.setAmount(amount);
        return wechatRequest;
    }

    private RefundService buildService(ManagerPaymentProperties.Wechat wechat) {
        Config config = new RSAAutoCertificateConfig.Builder()
                .merchantId(wechat.getMchId())
                .privateKeyFromPath(wechat.getPrivateKeyPath())
                .merchantSerialNumber(wechat.getMerchantSerialNo())
                .apiV3Key(wechat.getApiV3Key())
                .build();
        return new RefundService.Builder().config(config).build();
    }

    private Long toCent(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).movePointRight(2).longValueExact();
    }

    private String mapRefundStatus(Status status) {
        if (Status.SUCCESS.equals(status)) {
            return "SUCCESS";
        }
        if (Status.PROCESSING.equals(status)) {
            return "PROCESSING";
        }
        return "FAILED";
    }

    private String pendingTradeNo(PaymentChannelRequest request) {
        return "WECHAT-PENDING-" + request.getRequestNo();
    }

    private boolean nonBlank(String value) {
        return value != null && value.trim().length() > 0;
    }
}
