package cn.fuguang.order.mq.listener;

import cn.fuguang.common.rocketmq.RocketMqConstants;
import cn.fuguang.order.biz.FlashSaleBiz;
import cn.fuguang.order.pojo.dto.FlashSaleDto;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;

import javax.annotation.Resource;

@RocketMQMessageListener(consumerGroup = RocketMqConstants.flashSaleConsumer
        , topic = RocketMqConstants.flashSaleTopic, consumeThreadNumber = 5)
@Slf4j
public class FlashSaleListener  implements RocketMQListener<MessageExt> {

    @Resource
    private FlashSaleBiz flashSaleBiz;


    @Override
    public void onMessage(MessageExt messageExt) {
        log.info("[FlashSaleListener] 收到消息:{}", JSONObject.toJSONString(messageExt));

        try {
            byte[] body = messageExt.getBody();
            FlashSaleDto flashSaleDto = JSONObject.parseObject(new String(body), FlashSaleDto.class);

            flashSaleBiz.processFlashSale(flashSaleDto);
        } catch (Exception e) {
            log.error("[FlashSaleListener] 处理消息失败", e);
        }


    }
}
