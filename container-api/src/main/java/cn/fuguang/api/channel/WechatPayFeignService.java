package cn.fuguang.api.channel;

import cn.fuguang.api.channel.dto.req.WxCreateOrderReqDTO;
import cn.fuguang.api.channel.dto.res.OauthTokenResDTO;
import cn.fuguang.api.channel.dto.res.WxCreateOrderResDTO;
import cn.fuguang.feign.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "container-channel", contextId = "wechat-pay")
public interface WechatPayFeignService {

    //BaseResponse

    @ResponseBody
    @PostMapping("/channel/wxCreateOrder")
    BaseResponse<WxCreateOrderResDTO> wxCreateOrder(@RequestBody WxCreateOrderReqDTO reqDTO);
}
