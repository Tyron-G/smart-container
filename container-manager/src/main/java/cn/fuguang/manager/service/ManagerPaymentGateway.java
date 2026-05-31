package cn.fuguang.manager.service;

import cn.fuguang.manager.mapper.PaymentOperationMapper;
import cn.fuguang.manager.service.payment.ExternalPaymentPendingAdapter;
import cn.fuguang.manager.service.payment.PaymentChannelAdapter;
import cn.fuguang.manager.service.payment.PaymentChannelRequest;
import cn.fuguang.manager.service.payment.PaymentChannelResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ManagerPaymentGateway {

    @Resource
    private PaymentOperationMapper paymentOperationMapper;

    private final Map<String, PaymentChannelAdapter> adapterMap = new HashMap<String, PaymentChannelAdapter>();

    public ManagerPaymentGateway(List<PaymentChannelAdapter> adapters) {
        for (PaymentChannelAdapter adapter : adapters) {
            adapterMap.put(adapter.channelType(), adapter);
        }
        adapterMap.putIfAbsent("ALIPAY", new ExternalPaymentPendingAdapter("ALIPAY"));
        adapterMap.putIfAbsent("WECHAT", new ExternalPaymentPendingAdapter("WECHAT"));
    }

    public PaymentResult find(String requestNo) {
        Map<String, Object> row = paymentOperationMapper.selectByRequestNo(requestNo);
        if (row == null || row.isEmpty()) {
            return null;
        }
        return new PaymentResult(requestNo, String.valueOf(row.get("status")),
                String.valueOf(row.get("channel_trade_no")),
                String.valueOf(row.get("channel_message")),
                row.get("error_code") == null ? null : String.valueOf(row.get("error_code")), true);
    }

    public PaymentResult execute(String requestNo, String orderNo, String operationType, BigDecimal amount, String reason, String operator, String channelType) {
        return execute(requestNo, orderNo, operationType, amount, null, reason, operator, channelType, null);
    }

    public PaymentResult execute(String requestNo, String orderNo, String operationType, BigDecimal amount, String reason, String operator, String channelType, String originalChannelTradeNo) {
        return execute(requestNo, orderNo, operationType, amount, null, reason, operator, channelType, originalChannelTradeNo);
    }

    public PaymentResult execute(String requestNo, String orderNo, String operationType, BigDecimal amount, BigDecimal originalOrderAmount, String reason, String operator, String channelType, String originalChannelTradeNo) {
        PaymentResult existing = find(requestNo);
        if (existing != null) {
            return existing;
        }

        String actualChannel = channelType == null || channelType.trim().length() == 0 ? "LOCAL_SIMULATED" : channelType.trim();
        PaymentChannelAdapter adapter = adapterMap.get(actualChannel);
        if (adapter == null) {
            // 2026-05-31: 未知真实通道不能降级成本地成功，必须闭锁为配置错误。
            PaymentChannelResult unsupported = new PaymentChannelResult("CONFIG_ERROR", actualChannel + "-PENDING-" + requestNo, "不支持的支付通道：" + actualChannel, null, null, "UNSUPPORTED_CHANNEL");
            paymentOperationMapper.insertPaymentOperation(requestNo, orderNo, operationType, actualChannel, amount, reason,
                    unsupported.getStatus(), unsupported.getChannelTradeNo(), unsupported.getChannelMessage(), unsupported.getErrorCode(),
                    unsupported.getRequestPayload(), unsupported.getResponsePayload(), operator);
            return new PaymentResult(requestNo, unsupported.getStatus(), unsupported.getChannelTradeNo(), unsupported.getChannelMessage(), unsupported.getErrorCode(), false);
        }
        PaymentChannelResult channelResult = adapter.execute(new PaymentChannelRequest(requestNo, orderNo, operationType, amount, originalOrderAmount, reason, operator, originalChannelTradeNo));

        paymentOperationMapper.insertPaymentOperation(requestNo, orderNo, operationType, actualChannel, amount, reason,
                channelResult.getStatus(), channelResult.getChannelTradeNo(), limit(channelResult.getChannelMessage(), 480), channelResult.getErrorCode(),
                channelResult.getRequestPayload(), channelResult.getResponsePayload(), operator);

        return new PaymentResult(requestNo, channelResult.getStatus(), channelResult.getChannelTradeNo(), limit(channelResult.getChannelMessage(), 480), channelResult.getErrorCode(), false);
    }

    public Map<String, Object> toMap(PaymentResult result) {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("requestNo", result.getRequestNo());
        data.put("status", result.getStatus());
        data.put("channelTradeNo", result.getChannelTradeNo());
        data.put("channelMessage", result.getChannelMessage());
        data.put("errorCode", result.getErrorCode());
        data.put("idempotent", result.isIdempotent());
        return data;
    }

    public static class PaymentResult {
        private final String requestNo;
        private final String status;
        private final String channelTradeNo;
        private final String channelMessage;
        private final String errorCode;
        private final boolean idempotent;

        public PaymentResult(String requestNo, String status, String channelTradeNo, String channelMessage, String errorCode, boolean idempotent) {
            this.requestNo = requestNo;
            this.status = status;
            this.channelTradeNo = channelTradeNo;
            this.channelMessage = channelMessage;
            this.errorCode = errorCode;
            this.idempotent = idempotent;
        }

        public String getRequestNo() {
            return requestNo;
        }

        public String getStatus() {
            return status;
        }

        public String getChannelTradeNo() {
            return channelTradeNo;
        }

        public String getChannelMessage() {
            return channelMessage;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public boolean isIdempotent() {
            return idempotent;
        }
    }

    private String limit(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
