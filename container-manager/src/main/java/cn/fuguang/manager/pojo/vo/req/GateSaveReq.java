package cn.fuguang.manager.pojo.vo.req;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class GateSaveReq implements Serializable {

    private static final long serialVersionUID = -1L;

    @Size(max = 80, message = "仓门ID长度不能超过80个字符")
    private String gateId;

    @NotBlank(message = "设备ID不能为空")
    @Size(max = 64, message = "设备ID长度不能超过64个字符")
    private String deviceId;

    @Size(max = 64, message = "仓门名称长度不能超过64个字符")
    private String gateName;

    @Size(max = 32, message = "仓门编码长度不能超过32个字符")
    private String gateCode;

    @Min(value = 0, message = "排序不能小于0")
    @Max(value = 999, message = "排序不能大于999")
    private Integer sort;

    @Pattern(regexp = "^(ONLINE|OFFLINE|ERROR|NORMAL|OFF_LINE|ABNORMAL|MAINTENANCE|INIT|)$", message = "仓门状态不支持")
    private String gateStatus;

    @Size(max = 255, message = "备注长度不能超过255个字符")
    private String remarks;

    @Size(max = 64, message = "操作人长度不能超过64个字符")
    private String operator;
}
