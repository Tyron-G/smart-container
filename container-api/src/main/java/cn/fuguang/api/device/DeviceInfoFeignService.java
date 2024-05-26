package cn.fuguang.api.device;

import cn.fuguang.api.device.req.CheckDeviceStatusReqDTO;
import cn.fuguang.feign.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "container-device")
public interface DeviceInfoFeignService {

    BaseResponse checkDeviceStatus(@RequestBody CheckDeviceStatusReqDTO reqDTO);
}
