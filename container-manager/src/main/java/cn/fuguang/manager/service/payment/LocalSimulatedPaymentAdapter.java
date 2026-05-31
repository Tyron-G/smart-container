package cn.fuguang.manager.service.payment;

import org.springframework.stereotype.Component;

@Component
public class LocalSimulatedPaymentAdapter implements PaymentChannelAdapter {

    @Override
    public String channelType() {
        return "LOCAL_SIMULATED";
    }

    @Override
    public PaymentChannelResult execute(PaymentChannelRequest request) {
        // 2026-05-31: 本地模拟通道只记录可审计流水，不触发任何外部资金划转。
        return new PaymentChannelResult(
                "SUCCESS",
                "PAY-" + request.getRequestNo(),
                "本地模拟通道已记录，未触发外部真实资金划转");
    }
}
