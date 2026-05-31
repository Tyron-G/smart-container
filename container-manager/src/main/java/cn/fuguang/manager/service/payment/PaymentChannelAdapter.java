package cn.fuguang.manager.service.payment;

public interface PaymentChannelAdapter {

    String channelType();

    PaymentChannelResult execute(PaymentChannelRequest request);
}
