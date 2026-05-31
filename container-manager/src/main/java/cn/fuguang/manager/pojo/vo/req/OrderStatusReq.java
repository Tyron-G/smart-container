package cn.fuguang.manager.pojo.vo.req;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class OrderStatusReq implements Serializable {

    private static final long serialVersionUID = -1L;

    @NotBlank(message = "订单号不能为空")
    @Size(max = 64, message = "订单号长度不能超过64个字符")
    private String orderNo;

    @NotBlank(message = "订单状态不能为空")
    @Pattern(regexp = "^(FULLY_PAY|CANCEL|EXCEPTION)$", message = "订单状态仅支持 FULLY_PAY/CANCEL/EXCEPTION")
    private String status;

    @Size(max = 64, message = "操作人长度不能超过64个字符")
    private String operator;
}
