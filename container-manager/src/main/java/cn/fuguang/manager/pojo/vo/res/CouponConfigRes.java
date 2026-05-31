package cn.fuguang.manager.pojo.vo.res;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CouponConfigRes implements Serializable {

    private static final long serialVersionUID = -1L;

    private Long id;

    private String couponConfigId;

    private String productUid;

    private String couponType;

    private BigDecimal couponMoney;

    private String couponAllCategories;

    private Long couponCount;

    private Long couponValidDate;

    private String couponImage;

    private String couponName;

    private Long couponReceiveValidDate;

    private BigDecimal couponTotalMoney;

    private Long couponCustomerLimit;

    private Long couponCustomerAmountLimit;

    private Long couponIssuedCount;

    private String couponDeadline;

    private String couponDesc;

    private String remarks;

    private String status;

    private String createTime;

    private String updateTime;
}
