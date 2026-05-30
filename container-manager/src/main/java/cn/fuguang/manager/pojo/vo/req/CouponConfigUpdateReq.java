package cn.fuguang.manager.pojo.vo.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class CouponConfigUpdateReq extends CouponConfigReq implements Serializable {

    private static final long serialVersionUID = -1L;

    private String couponConfigId;
}
