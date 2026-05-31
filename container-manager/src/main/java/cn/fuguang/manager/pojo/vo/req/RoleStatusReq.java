package cn.fuguang.manager.pojo.vo.req;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class RoleStatusReq implements Serializable {

    private static final long serialVersionUID = -1L;

    @NotBlank(message = "角色ID不能为空")
    @Size(max = 64, message = "角色ID长度不能超过64个字符")
    private String roleId;

    @NotBlank(message = "角色状态不能为空")
    @Pattern(regexp = "^(ACTIVE|DISABLED)$", message = "角色状态仅支持 ACTIVE/DISABLED")
    private String status;

    @Size(max = 64, message = "操作人长度不能超过64个字符")
    private String operator;
}
