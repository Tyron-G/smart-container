package cn.fuguang.manager.pojo.vo.req;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class PermissionStatusReq implements Serializable {

    private static final long serialVersionUID = -1L;

    @NotBlank(message = "权限ID不能为空")
    @Size(max = 64, message = "权限ID长度不能超过64个字符")
    private String permissionId;

    @NotBlank(message = "权限状态不能为空")
    @Pattern(regexp = "^(ACTIVE|DISABLED)$", message = "权限状态仅支持 ACTIVE/DISABLED")
    private String status;

    @Size(max = 64, message = "操作人长度不能超过64个字符")
    private String operator;
}
