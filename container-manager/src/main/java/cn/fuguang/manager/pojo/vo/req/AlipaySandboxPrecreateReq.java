package cn.fuguang.manager.pojo.vo.req;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;

@Data
public class AlipaySandboxPrecreateReq implements Serializable {

    private static final long serialVersionUID = -1L;

    @Size(max = 64, message = "商户订单号长度不能超过64个字符")
    private String outTradeNo;

    @DecimalMin(value = "0.01", message = "金额必须大于0")
    @Digits(integer = 10, fraction = 2, message = "金额最多10位整数和2位小数")
    private BigDecimal amount;

    @Size(max = 128, message = "订单标题长度不能超过128个字符")
    private String subject;
}
