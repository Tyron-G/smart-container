package cn.fuguang.manager.pojo.vo.res;

import lombok.Data;

import java.io.Serializable;

@Data
public class CouponConfigOverviewRes implements Serializable {

    private static final long serialVersionUID = -1L;

    private Long total;

    private Long cashTotal;

    private Long groupTotal;

    private Long issuedTotal;
}
