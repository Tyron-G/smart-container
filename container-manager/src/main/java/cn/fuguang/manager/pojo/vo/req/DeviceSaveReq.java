package cn.fuguang.manager.pojo.vo.req;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class DeviceSaveReq implements Serializable {

    private static final long serialVersionUID = -1L;

    @Size(max = 64, message = "设备ID长度不能超过64个字符")
    private String deviceId;

    @NotBlank(message = "设备SN不能为空")
    @Size(max = 64, message = "设备SN长度不能超过64个字符")
    private String deviceSn;

    @Size(max = 64, message = "设备名称长度不能超过64个字符")
    private String deviceName;

    @Pattern(regexp = "^(ONLINE|OFFLINE|ERROR|NORMAL|OFF_LINE|ABNORMAL|MAINTENANCE|INIT|)$", message = "设备状态不支持")
    private String deviceStatus;

    @Size(max = 64, message = "设备类型长度不能超过64个字符")
    private String deviceType;

    @Size(max = 64, message = "省份长度不能超过64个字符")
    private String province;

    @Size(max = 64, message = "城市长度不能超过64个字符")
    private String city;

    @Size(max = 64, message = "区县长度不能超过64个字符")
    private String county;

    @Size(max = 128, message = "小区长度不能超过128个字符")
    private String community;

    @Size(max = 255, message = "地址长度不能超过255个字符")
    private String address;

    @Size(max = 255, message = "备注长度不能超过255个字符")
    private String remarks;

    @Size(max = 64, message = "操作人长度不能超过64个字符")
    private String operator;
}
