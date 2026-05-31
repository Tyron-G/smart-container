package cn.fuguang.manager.service;

import cn.fuguang.entity.CouponConfigEntity;
import cn.fuguang.manager.pojo.vo.req.CouponConfigPageReq;

import java.util.List;

public interface CouponService {

    void insert(CouponConfigEntity couponConfigEntity);

    CouponConfigEntity queryByCouponConfigId(String couponConfigId);

    void updateByCouponConfigId(CouponConfigEntity couponConfigEntity);

    void updateStatusByCouponConfigId(String couponConfigId, String status);

    long countByPageReq(CouponConfigPageReq req);

    List<CouponConfigEntity> queryByPageReq(CouponConfigPageReq req);

    long sumIssuedCountByPageReq(CouponConfigPageReq req);
}
