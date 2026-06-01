package cn.fuguang.order.pojo.vo.res;

import lombok.Data;

import java.io.Serializable;

@Data
public class WechatLoginRes implements Serializable {

    private static final long serialVersionUID = -1L;

    private Boolean bindStatus;

    private String openId;

    private String mobile;

    private String customerId;

    private String accessToken;
}
