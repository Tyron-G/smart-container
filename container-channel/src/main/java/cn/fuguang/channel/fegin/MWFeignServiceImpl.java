package cn.fuguang.channel.fegin;

import cn.fuguang.api.channel.MWFeignService;
import cn.fuguang.api.channel.dto.req.SendSmsReqDTO;
import cn.fuguang.feign.BaseResponse;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MWFeignServiceImpl implements MWFeignService {
    @Override
    public BaseResponse sendSms(SendSmsReqDTO reqDTO) {
        log.info("通道接收发送短信参数:{}", JSONObject.toJSONString(reqDTO));
        try {
            SendSmsRes sendRes = send(smsReq);
            if (!Constant.Status.STATUS_SUCCESS.equalsIgnoreCase(String.valueOf(sendRes.getResult()))) {
                return BaseResponse.onFail(String.valueOf(sendRes.getResult()), sendRes.getDesc());
            }
        } catch (Exception e) {
            log.error("通道接收发送短信 系统异常" + e);
            return BaseResponse.fail(e.getMessage());
        }
        log.info("通道发送短信成功");
        return BaseResponse.success();
    }
}
