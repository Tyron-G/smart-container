package cn.fuguang.manager.biz;

import cn.fuguang.manager.pojo.vo.req.CouponConfigPageReq;
import cn.fuguang.manager.pojo.vo.req.CouponConfigReq;
import cn.fuguang.manager.pojo.vo.req.CouponConfigStatusReq;
import cn.fuguang.manager.pojo.vo.req.CouponConfigUpdateReq;
import cn.fuguang.manager.pojo.vo.res.CouponConfigOverviewRes;
import cn.fuguang.manager.pojo.vo.res.CouponConfigPageRes;
import cn.fuguang.manager.pojo.vo.res.CouponConfigRes;

public interface CouponBiz {
    void addCouponConfig(CouponConfigReq req);

    CouponConfigRes getCouponConfigDetail(String couponConfigId);

    void updateCouponConfig(CouponConfigUpdateReq req);

    void changeCouponConfigStatus(CouponConfigStatusReq req);

    CouponConfigPageRes queryCouponConfigPage(CouponConfigPageReq req);

    CouponConfigOverviewRes queryCouponConfigOverview(CouponConfigPageReq req);
}
