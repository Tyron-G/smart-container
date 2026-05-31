package cn.fuguang.manager.pojo.vo.req;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class DeviceCommandReq implements Serializable {

    private static final long serialVersionUID = -1L;

    @NotBlank(message = "设备ID不能为空")
    @Size(max = 64, message = "设备ID长度不能超过64个字符")
    private String deviceId;

    @Size(max = 80, message = "仓门ID长度不能超过80个字符")
    private String gateId;

    @Size(max = 64, message = "操作人长度不能超过64个字符")
    private String operator;
}
