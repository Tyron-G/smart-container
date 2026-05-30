package cn.fuguang.manager.biz.impl;

import cn.fuguang.entity.CouponConfigEntity;
import cn.fuguang.manager.biz.CouponBiz;
import cn.fuguang.manager.pojo.vo.req.CouponConfigPageReq;
import cn.fuguang.manager.pojo.vo.req.CouponConfigReq;
import cn.fuguang.manager.pojo.vo.req.CouponConfigUpdateReq;
import cn.fuguang.manager.pojo.vo.res.CouponConfigPageRes;
import cn.fuguang.manager.pojo.vo.res.CouponConfigRes;
import cn.fuguang.manager.service.CouponService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class CouponBizImpl implements CouponBiz {

    @Resource
    private CouponService couponService;

    @Override
    public void addCouponConfig(CouponConfigReq req) {
        CouponConfigEntity couponConfigEntity = new CouponConfigEntity();
        BeanUtil.copyProperties(req, couponConfigEntity);
        couponConfigEntity.setCouponConfigId(IdUtil.simpleUUID());
        couponService.insert(couponConfigEntity);
    }

    @Override
    public CouponConfigRes getCouponConfigDetail(String couponConfigId) {
        if (StrUtil.isBlank(couponConfigId)) {
            return null;
        }
        return convertToRes(couponService.queryByCouponConfigId(couponConfigId));
    }

    @Override
    public void updateCouponConfig(CouponConfigUpdateReq req) {
        CouponConfigEntity couponConfigEntity = new CouponConfigEntity();
        BeanUtil.copyProperties(req, couponConfigEntity);
        couponService.updateByCouponConfigId(couponConfigEntity);
    }

    @Override
    public CouponConfigPageRes queryCouponConfigPage(CouponConfigPageReq req) {
        CouponConfigPageReq safeReq = buildSafePageReq(req);
        long total = couponService.countByPageReq(safeReq);
        List<CouponConfigEntity> entityList = total == 0 ? new ArrayList<>() : couponService.queryByPageReq(safeReq);
        List<CouponConfigRes> records = new ArrayList<>();
        for (CouponConfigEntity entity : entityList) {
            records.add(convertToRes(entity));
        }

        CouponConfigPageRes res = new CouponConfigPageRes();
        res.setRecords(records);
        res.setPageNum(safeReq.getPageNum());
        res.setPageSize(safeReq.getPageSize());
        res.setTotal(total);
        return res;
    }

    private CouponConfigPageReq buildSafePageReq(CouponConfigPageReq req) {
        CouponConfigPageReq safeReq = req == null ? new CouponConfigPageReq() : req;
        if (safeReq.getPageNum() == null || safeReq.getPageNum() < 1) {
            safeReq.setPageNum(1);
        }
        if (safeReq.getPageSize() == null || safeReq.getPageSize() < 1) {
            safeReq.setPageSize(10);
        }
        if (safeReq.getPageSize() > 100) {
            safeReq.setPageSize(100);
        }
        return safeReq;
    }

    private CouponConfigRes convertToRes(CouponConfigEntity entity) {
        if (entity == null) {
            return null;
        }
        CouponConfigRes res = new CouponConfigRes();
        BeanUtil.copyProperties(entity, res);
        if (entity.getCouponDeadline() != null) {
            res.setCouponDeadline(DateUtil.formatDateTime(entity.getCouponDeadline()));
        }
        if (entity.getCreateTime() != null) {
            res.setCreateTime(DateUtil.formatDateTime(entity.getCreateTime()));
        }
        return res;
    }
}
