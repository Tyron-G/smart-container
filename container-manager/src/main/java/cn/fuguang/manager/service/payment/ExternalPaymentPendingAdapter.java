package cn.fuguang.manager.service.payment;

public class ExternalPaymentPendingAdapter implements PaymentChannelAdapter {

    private final String channelType;

    public ExternalPaymentPendingAdapter(String channelType) {
        this.channelType = channelType;
    }

    @Override
    public String channelType() {
        return channelType;
    }

    @Override
    public PaymentChannelResult execute(PaymentChannelRequest request) {
        // 2026-05-31: 真实通道未配置前只记录待配置状态，避免误判资金已处理。
        return new PaymentChannelResult(
                "PENDING_CONFIG",
                channelType + "-PENDING-" + request.getRequestNo(),
                channelType + "真实支付通道未配置，已记录请求但未触发资金划转");
    }
}
