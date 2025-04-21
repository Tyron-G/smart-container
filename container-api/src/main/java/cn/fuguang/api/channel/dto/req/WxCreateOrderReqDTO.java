package cn.fuguang.api.channel.dto.req;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class WxCreateOrderReqDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    private String userId;

    private BigDecimal amount;

    private String orderNo;
}
