package cn.fuguang.manager.pojo.vo.req;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class DeviceEventProcessReq implements Serializable {

    private static final long serialVersionUID = -1L;

    @NotBlank(message = "事件ID不能为空")
    @Size(max = 64, message = "事件ID长度不能超过64个字符")
    private String id;

    @Size(max = 500, message = "处理备注长度不能超过500个字符")
    private String remark;

    @Size(max = 64, message = "操作人长度不能超过64个字符")
    private String operator;
}
