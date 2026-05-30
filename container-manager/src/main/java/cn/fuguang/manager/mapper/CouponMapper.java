package cn.fuguang.manager.mapper;

import cn.fuguang.entity.CouponConfigEntity;
import cn.fuguang.manager.pojo.vo.req.CouponConfigPageReq;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CouponMapper {
    void insert(CouponConfigEntity couponConfigEntity);

    CouponConfigEntity selectByCouponConfigId(@Param("couponConfigId") String couponConfigId);

    int updateByCouponConfigId(CouponConfigEntity couponConfigEntity);

    long countByPageReq(CouponConfigPageReq req);

    List<CouponConfigEntity> queryByPageReq(CouponConfigPageReq req);
}
