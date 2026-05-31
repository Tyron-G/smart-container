package cn.fuguang.manager.pojo.vo.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
public class CouponConfigUpdateReq extends CouponConfigReq implements Serializable {

    private static final long serialVersionUID = -1L;

    @NotBlank(message = "优惠券配置ID不能为空")
    @Size(max = 64, message = "优惠券配置ID长度不能超过64个字符")
    private String couponConfigId;
}
