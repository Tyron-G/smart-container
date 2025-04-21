package cn.fuguang.order.biz.impl;

import cn.fuguang.api.channel.WechatPayFeignService;
import cn.fuguang.api.channel.dto.req.WxCreateOrderReqDTO;
import cn.fuguang.api.channel.dto.res.OauthTokenResDTO;
import cn.fuguang.api.channel.dto.res.WxCreateOrderResDTO;
import cn.fuguang.common.redis.service.RedisService;
import cn.fuguang.common.rocketmq.RocketMqConstants;
import cn.fuguang.common.rocketmq.service.RocketMqService;
import cn.fuguang.entity.FlashSaleEntity;
import cn.fuguang.exception.ContainerException;
import cn.fuguang.feign.BaseResponse;
import cn.fuguang.order.biz.FlashSaleBiz;
import cn.fuguang.order.mapper.FlashSaleMapper;
import cn.fuguang.order.pojo.dto.FlashSaleDto;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Service
public class FlashSaleBizImpl implements FlashSaleBiz {

    @Resource
    private RedisService redisService;

    @Resource
    private RocketMqService rocketMqService;

    @Resource
    private FlashSaleMapper flashSaleMapper;

    @Resource
    private WechatPayFeignService wechatPayFeignService;

    private volatile List<String> saledList;

    public void sale(FlashSaleDto flashSaleDto) {

        if (saledList.contains(flashSaleDto.getSaleId())) {
            throw ContainerException.PARAMS_ERROR.newInstance("已抢完");
        }

        if (redisService.isMember("SALE_" + flashSaleDto.getSaleId(), flashSaleDto.getUserId())){
            throw ContainerException.PARAMS_ERROR.newInstance("已抢过");
        }

        // 1. 扣减库存
        Long decrement = redisService.decrement(flashSaleDto.getSaleId(), 1);

        if (decrement < 0) {
            saledList.add(flashSaleDto.getSaleId());
            throw ContainerException.PARAMS_ERROR.newInstance("已抢完");
        }

        redisService.sSet("SALE_" + flashSaleDto.getSaleId(), flashSaleDto.getUserId());

        //发mq
        rocketMqService.sendMessage(RocketMqConstants.flashSaleTopic, JSONObject.toJSONString(flashSaleDto));

    }

    @Override
    public void processFlashSale(FlashSaleDto flashSaleDto) {

        //1.创建订单实体
        FlashSaleEntity entity = this.build(flashSaleDto);

        //2.入库
        flashSaleMapper.insert(entity);



    }

    @Override
    public String createSaleOrder(FlashSaleDto flashSaleDto) {
        //1.查询订单
        FlashSaleEntity entity = flashSaleMapper.selectByParams(flashSaleDto.getSaleId(), flashSaleDto.getUserId());

        //2.判断订单是否存在
        if (entity == null) {
            throw ContainerException.PARAMS_ERROR.newInstance("订单不存在");
        }

        //3组装请求微信参数
        WxCreateOrderReqDTO wxCreateOrderReqDTO = this.buildWxCreateOrderDTO(entity);

        //4.调用微信下单接口
        BaseResponse<WxCreateOrderResDTO> response = null;
        try {
            response = wechatPayFeignService.wxCreateOrder(wxCreateOrderReqDTO);
        } catch (Exception e) {
            throw ContainerException.PARAMS_ERROR.newInstance("下单失败,请稍后重试");
        }

        if (!response.isSuccess()) {
            throw ContainerException.PARAMS_ERROR.newInstance("下单失败,请稍后重试");
        }

        //5.更新订单状态
        flashSaleMapper.updateOrderStatus(entity.getOrderNo(), "INIT", "PROCESSING");

        //6.发送延迟消息
        rocketMqService.sendDelayMessage(RocketMqConstants.flashSaleDelayTopic, JSONObject.toJSONString(entity), 3);

        return response.getData().getPackageDate();

    }

    private WxCreateOrderReqDTO buildWxCreateOrderDTO(FlashSaleEntity entity) {
        WxCreateOrderReqDTO wxCreateOrderReqDTO = new WxCreateOrderReqDTO();
        BeanUtils.copyProperties(entity, wxCreateOrderReqDTO);
        wxCreateOrderReqDTO.setAmount(entity.getPrice());
        return wxCreateOrderReqDTO;
    }


    private FlashSaleEntity build(FlashSaleDto flashSaleDto) {
        FlashSaleEntity flashSaleEntity = new FlashSaleEntity();
        flashSaleEntity.setSaleId(flashSaleDto.getSaleId());
        flashSaleEntity.setUserId(flashSaleDto.getUserId());
        flashSaleEntity.setOrderNo(IdUtil.fastSimpleUUID());
        flashSaleEntity.setUserId("INIT");
        //查询saleIdd对应的商品
        String productId = "xxx";

        //擦讯saleId对应的秒杀价格
        BigDecimal price = new BigDecimal("0.01");
        flashSaleEntity.setPrice(price);
        flashSaleEntity.setProductId(productId);
        return flashSaleEntity;
    }
}
