package cn.fuguang.manager.pojo.vo.req;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class PermissionSaveReq implements Serializable {

    private static final long serialVersionUID = -1L;

    @Size(max = 64, message = "权限ID长度不能超过64个字符")
    private String permissionId;

    @NotBlank(message = "权限名称不能为空")
    @Size(max = 64, message = "权限名称长度不能超过64个字符")
    private String permissionName;

    @NotBlank(message = "权限编码不能为空")
    @Pattern(regexp = "^[a-z][a-zA-Z0-9]*:[a-zA-Z][a-zA-Z0-9]*$", message = "权限编码格式应为 module:action")
    private String permissionCode;

    @Pattern(regexp = "^(MENU|ACTION|)$", message = "权限类型仅支持 MENU/ACTION")
    private String permissionType;

    @Size(max = 64, message = "菜单标识长度不能超过64个字符")
    private String menuKey;

    @Size(max = 64, message = "父级权限编码长度不能超过64个字符")
    private String parentCode;

    @Size(max = 255, message = "描述长度不能超过255个字符")
    private String description;

    @Min(value = 0, message = "排序不能小于0")
    @Max(value = 9999, message = "排序不能大于9999")
    private Integer sort;

    @Pattern(regexp = "^(ACTIVE|DISABLED|)$", message = "权限状态仅支持 ACTIVE/DISABLED")
    private String status;

    @Size(max = 64, message = "操作人长度不能超过64个字符")
    private String operator;
}
