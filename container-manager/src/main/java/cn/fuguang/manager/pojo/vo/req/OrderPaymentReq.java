package cn.fuguang.manager.pojo.vo.req;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class OrderPaymentReq implements Serializable {

    private static final long serialVersionUID = -1L;

    @Size(max = 64, message = "请求号长度不能超过64个字符")
    private String requestNo;

    @NotBlank(message = "订单号不能为空")
    @Size(max = 64, message = "订单号长度不能超过64个字符")
    private String orderNo;

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    @Digits(integer = 10, fraction = 2, message = "金额最多10位整数和2位小数")
    private BigDecimal amount;

    @NotBlank(message = "原因不能为空")
    @Size(min = 2, max = 255, message = "原因长度必须在2到255个字符之间")
    private String reason;

    @Size(max = 64, message = "操作人长度不能超过64个字符")
    private String operator;

    @Pattern(regexp = "^(LOCAL_SIMULATED|ALIPAY|WECHAT|)$", message = "支付通道仅支持 LOCAL_SIMULATED/ALIPAY/WECHAT")
    private String channelType;

    @Size(max = 128, message = "原渠道交易号长度不能超过128个字符")
    private String originalChannelTradeNo;
}
