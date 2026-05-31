package cn.fuguang.manager.pojo.vo.req;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class CouponConfigStatusReq implements Serializable {

    private static final long serialVersionUID = -1L;

    @NotBlank(message = "优惠券配置ID不能为空")
    @Size(max = 64, message = "优惠券配置ID长度不能超过64个字符")
    private String couponConfigId;

    @NotBlank(message = "优惠券状态不能为空")
    @Pattern(regexp = "^(ACTIVE|DISABLED|DELETED)$", message = "优惠券状态仅支持 ACTIVE/DISABLED/DELETED")
    private String status;
}
