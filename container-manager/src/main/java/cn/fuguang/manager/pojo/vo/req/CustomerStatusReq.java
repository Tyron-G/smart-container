package cn.fuguang.manager.pojo.vo.req;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class CustomerStatusReq implements Serializable {

    private static final long serialVersionUID = -1L;

    @NotBlank(message = "客户ID不能为空")
    @Size(max = 64, message = "客户ID长度不能超过64个字符")
    private String customerId;

    @NotBlank(message = "用户状态不能为空")
    @Pattern(regexp = "^(ACTIVE|DISABLED|BLACKLIST)$", message = "用户状态仅支持 ACTIVE/DISABLED/BLACKLIST")
    private String status;

    @Size(max = 64, message = "操作人长度不能超过64个字符")
    private String operator;
}
