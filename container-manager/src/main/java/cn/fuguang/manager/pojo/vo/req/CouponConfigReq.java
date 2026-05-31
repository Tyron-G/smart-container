package cn.fuguang.manager.pojo.vo.req;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class CouponConfigReq implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 商品id
     */
    @NotBlank(message = "商品ID不能为空")
    @Size(max = 64, message = "商品ID长度不能超过64个字符")
    private String productUid;

    /**
     * 优惠券类型
     */
    @NotBlank(message = "优惠券类型不能为空")
    @Pattern(regexp = "^(CASH|GROUP)$", message = "优惠券类型仅支持 CASH/GROUP")
    private String couponType;

    /**
     * 优惠券金额
     */
    @NotNull(message = "优惠券金额不能为空")
    @DecimalMin(value = "0.01", message = "优惠券金额必须大于0")
    @Digits(integer = 10, fraction = 2, message = "优惠券金额最多10位整数和2位小数")
    private BigDecimal couponMoney;

    /**
     * 现金券是否全品类
     */
    @Pattern(regexp = "^(YES|NO|Y|N|true|false|)$", message = "现金券全品类标识不支持")
    private String couponAllCategories;

    /**
     * 优惠券数量
     */
    @NotNull(message = "优惠券数量不能为空")
    @Min(value = 1, message = "优惠券数量不能小于1")
    @Max(value = 1000000, message = "优惠券数量不能超过1000000")
    private Long couponCount;

    /**
     * 优惠券有效期 单位天
     */
    @NotNull(message = "优惠券有效期不能为空")
    @Min(value = 1, message = "优惠券有效期不能小于1天")
    @Max(value = 3650, message = "优惠券有效期不能超过3650天")
    private Long couponValidDate;

    /**
     * 优惠券图片
     */
    @Size(max = 512, message = "优惠券图片地址长度不能超过512个字符")
    private String couponImage;

    /**
     * 优惠券名称
     */
    @NotBlank(message = "优惠券名称不能为空")
    @Size(max = 64, message = "优惠券名称长度不能超过64个字符")
    private String couponName;

    /**
     * 领取有效期
     */
    @Min(value = 1, message = "领取有效期不能小于1天")
    @Max(value = 3650, message = "领取有效期不能超过3650天")
    private Long couponReceiveValidDate;

    /**
     * 优惠券总金额
     */
    @DecimalMin(value = "0.00", message = "优惠券总金额不能小于0")
    @Digits(integer = 10, fraction = 2, message = "优惠券总金额最多10位整数和2位小数")
    private BigDecimal couponTotalMoney;

    /**
     * 总用户限制
     */
    @Min(value = 0, message = "总用户限制不能小于0")
    private Long couponCustomerLimit;

    /**
     * 单用户单次优惠券总数量限制
     */
    @Min(value = 0, message = "单用户数量限制不能小于0")
    private Long couponCustomerAmountLimit;

    /**
     * 已发放张数
     */
    @Min(value = 0, message = "已发放张数不能小于0")
    private Long couponIssuedCount;

    /**
     * 团购截止时间 (团购券字段)
     */
    @Size(max = 32, message = "团购截止时间长度不能超过32个字符")
    private String couponDeadline;

    /**
     * 券说明
     */
    @Size(max = 512, message = "券说明长度不能超过512个字符")
    private String couponDesc;

    /**
     * 备注
     */
    @Size(max = 255, message = "备注长度不能超过255个字符")
    private String remarks;
}
