package cn.fuguang.manager.pojo.vo.req;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class CustomerSaveReq implements Serializable {

    private static final long serialVersionUID = -1L;

    @Size(max = 64, message = "客户ID长度不能超过64个字符")
    private String customerId;

    @Size(max = 64, message = "用户名长度不能超过64个字符")
    private String userName;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;

    @Size(max = 128, message = "支付宝OpenID长度不能超过128个字符")
    private String aliOpenId;

    @Size(max = 128, message = "微信OpenID长度不能超过128个字符")
    private String wechatOpenId;

    @Size(max = 255, message = "备注长度不能超过255个字符")
    private String remarks;

    @Size(max = 64, message = "操作人长度不能超过64个字符")
    private String operator;
}
