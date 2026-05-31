package cn.fuguang.manager.service.payment;

public class PaymentChannelResult {

    private final String status;
    private final String channelTradeNo;
    private final String channelMessage;
    private final String requestPayload;
    private final String responsePayload;
    private final String errorCode;

    public PaymentChannelResult(String status, String channelTradeNo, String channelMessage) {
        this(status, channelTradeNo, channelMessage, null, null, null);
    }

    public PaymentChannelResult(String status, String channelTradeNo, String channelMessage, String requestPayload, String responsePayload, String errorCode) {
        this.status = status;
        this.channelTradeNo = channelTradeNo;
        this.channelMessage = channelMessage;
        this.requestPayload = requestPayload;
        this.responsePayload = responsePayload;
        this.errorCode = errorCode;
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

    public String getRequestPayload() {
        return requestPayload;
    }

    public String getResponsePayload() {
        return responsePayload;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
