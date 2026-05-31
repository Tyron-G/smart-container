package cn.fuguang.manager.pojo.vo.req;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.Size;

@Data
public class OrderExceptionHandleReq implements Serializable {

    private static final long serialVersionUID = -1L;

    @Size(max = 64, message = "异常编号长度不能超过64个字符")
    private String exceptionNo;

    @Size(max = 64, message = "订单号长度不能超过64个字符")
    private String orderNo;

    @Size(max = 500, message = "处理结果长度不能超过500个字符")
    private String result;

    @Size(max = 64, message = "操作人长度不能超过64个字符")
    private String operator;
}
