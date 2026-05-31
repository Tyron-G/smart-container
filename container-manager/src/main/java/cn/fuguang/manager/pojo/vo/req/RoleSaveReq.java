package cn.fuguang.manager.pojo.vo.req;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class RoleSaveReq implements Serializable {

    private static final long serialVersionUID = -1L;

    @Size(max = 64, message = "角色ID长度不能超过64个字符")
    private String roleId;

    @NotBlank(message = "角色名称不能为空")
    @Size(max = 64, message = "角色名称长度不能超过64个字符")
    private String roleName;

    @NotBlank(message = "角色编码不能为空")
    @Pattern(regexp = "^[A-Z0-9_:-]{2,64}$", message = "角色编码仅支持大写字母、数字、下划线、冒号和横线")
    private String roleCode;

    @Size(max = 255, message = "描述长度不能超过255个字符")
    private String description;

    @Pattern(regexp = "^(ACTIVE|DISABLED|)$", message = "角色状态仅支持 ACTIVE/DISABLED")
    private String status;

    @Size(max = 64, message = "操作人长度不能超过64个字符")
    private String operator;
}
