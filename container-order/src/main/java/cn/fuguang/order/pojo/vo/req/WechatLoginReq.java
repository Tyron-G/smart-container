package cn.fuguang.order.pojo.vo.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class WechatLoginReq implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 2026-06-01：wx.login 获取的一次性 code。
     */
    @NotEmpty(message = "code not empty")
    private String code;

    /**
     * 2026-06-01：本地联调或首次绑定时传入的客户编号。
     */
    private String customerId;

    /**
     * 2026-06-01：本地联调展示用手机号，不参与微信换 openId。
     */
    private String mobile;
}
