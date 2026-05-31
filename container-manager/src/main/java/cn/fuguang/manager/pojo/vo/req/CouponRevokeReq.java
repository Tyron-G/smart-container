package cn.fuguang.manager.pojo.vo.req;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CouponRevokeReq implements Serializable {

    private static final long serialVersionUID = -1L;

    @NotBlank(message = "发放记录编号不能为空")
    @Size(max = 64, message = "发放记录编号长度不能超过64个字符")
    private String issueNo;

    @Size(max = 64, message = "操作人长度不能超过64个字符")
    private String operator;
}
