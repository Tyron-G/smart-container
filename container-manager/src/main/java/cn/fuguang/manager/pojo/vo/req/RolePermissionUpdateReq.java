package cn.fuguang.manager.pojo.vo.req;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RolePermissionUpdateReq implements Serializable {

    private static final long serialVersionUID = -1L;

    @NotBlank(message = "角色ID不能为空")
    @Size(max = 64, message = "角色ID长度不能超过64个字符")
    private String roleId;

    @NotNull(message = "权限编码列表不能为空")
    @Size(max = 200, message = "单次最多保存200个权限点")
    private List<String> permissionCodes;

    @Size(max = 64, message = "操作人长度不能超过64个字符")
    private String operator;
}
