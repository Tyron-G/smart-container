package cn.fuguang.manager.service.impl;

import cn.fuguang.entity.CouponConfigEntity;
import cn.fuguang.exception.ContainerException;
import cn.fuguang.manager.mapper.CouponMapper;
import cn.fuguang.manager.pojo.vo.req.CouponConfigPageReq;
import cn.fuguang.manager.service.CouponService;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class CouponServiceImpl implements CouponService {

    @Resource
    private CouponMapper couponMapper;
    @Override
    public void insert(CouponConfigEntity couponConfigEntity) {
        try {
            couponMapper.insert(couponConfigEntity);
        } catch (Exception e) {
            log.error("[coupon] insert error couponConfigEntity:" + JSONObject.toJSONString(couponConfigEntity), e);
            throw ContainerException.DATABASE_INERT_ERROR.newInstance("[coupon] insert error");
        }
    }

    @Override
    public CouponConfigEntity queryByCouponConfigId(String couponConfigId) {
        try {
            return couponMapper.selectByCouponConfigId(couponConfigId);
        } catch (Exception e) {
            log.error("[coupon] queryByCouponConfigId error couponConfigId:" + couponConfigId, e);
            throw ContainerException.DATABASE_QUERY_ERROR.newInstance("[coupon] query detail error");
        }
    }

    @Override
    public void updateByCouponConfigId(CouponConfigEntity couponConfigEntity) {
        try {
            int affectedRows = couponMapper.updateByCouponConfigId(couponConfigEntity);
            if (affectedRows == 0) {
                throw ContainerException.DATE_NOT_EXIST_ERROR.newInstance("[coupon] coupon config not exist");
            }
        } catch (ContainerException e) {
            throw e;
        } catch (Exception e) {
            log.error("[coupon] updateByCouponConfigId error couponConfigEntity:" + JSONObject.toJSONString(couponConfigEntity), e);
            throw ContainerException.DATABASE_UPDATE_ERROR.newInstance("[coupon] update error");
        }
    }

    @Override
    public void updateStatusByCouponConfigId(String couponConfigId, String status) {
        try {
            int affectedRows = couponMapper.updateStatusByCouponConfigId(couponConfigId, status);
            if (affectedRows == 0) {
                throw ContainerException.DATE_NOT_EXIST_ERROR.newInstance("[coupon] coupon config not exist");
            }
        } catch (ContainerException e) {
            throw e;
        } catch (Exception e) {
            log.error("[coupon] updateStatusByCouponConfigId error couponConfigId:" + couponConfigId + ", status:" + status, e);
            throw ContainerException.DATABASE_UPDATE_ERROR.newInstance("[coupon] update status error");
        }
    }

    @Override
    public long countByPageReq(CouponConfigPageReq req) {
        try {
            return couponMapper.countByPageReq(req);
        } catch (Exception e) {
            log.error("[coupon] countByPageReq error req:" + JSONObject.toJSONString(req), e);
            throw ContainerException.DATABASE_QUERY_ERROR.newInstance("[coupon] count page error");
        }
    }

    @Override
    public List<CouponConfigEntity> queryByPageReq(CouponConfigPageReq req) {
        try {
            return couponMapper.queryByPageReq(req);
        } catch (Exception e) {
            log.error("[coupon] queryByPageReq error req:" + JSONObject.toJSONString(req), e);
            throw ContainerException.DATABASE_QUERY_ERROR.newInstance("[coupon] query page error");
        }
    }

    @Override
    public long sumIssuedCountByPageReq(CouponConfigPageReq req) {
        try {
            return couponMapper.sumIssuedCountByPageReq(req);
        } catch (Exception e) {
            log.error("[coupon] sumIssuedCountByPageReq error req:" + JSONObject.toJSONString(req), e);
            throw ContainerException.DATABASE_QUERY_ERROR.newInstance("[coupon] sum issued count error");
        }
    }
}
