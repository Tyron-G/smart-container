package cn.fuguang.manager.service.payment;

import java.math.BigDecimal;

public class PaymentChannelRequest {

    private final String requestNo;
    private final String orderNo;
    private final String operationType;
    private final BigDecimal amount;
    private final BigDecimal originalOrderAmount;
    private final String reason;
    private final String operator;
    private final String originalChannelTradeNo;

    public PaymentChannelRequest(String requestNo, String orderNo, String operationType, BigDecimal amount, BigDecimal originalOrderAmount, String reason, String operator, String originalChannelTradeNo) {
        this.requestNo = requestNo;
        this.orderNo = orderNo;
        this.operationType = operationType;
        this.amount = amount;
        this.originalOrderAmount = originalOrderAmount;
        this.reason = reason;
        this.operator = operator;
        this.originalChannelTradeNo = originalChannelTradeNo;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getOperationType() {
        return operationType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getOriginalOrderAmount() {
        return originalOrderAmount;
    }

    public String getReason() {
        return reason;
    }

    public String getOperator() {
        return operator;
    }

    public String getOriginalChannelTradeNo() {
        return originalChannelTradeNo;
    }
}
