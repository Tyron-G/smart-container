package cn.fuguang.manager.controller;

import cn.fuguang.manager.biz.CouponBiz;
import cn.fuguang.manager.pojo.vo.req.CouponConfigPageReq;
import cn.fuguang.manager.pojo.vo.req.CouponConfigReq;
import cn.fuguang.manager.pojo.vo.req.CouponConfigStatusReq;
import cn.fuguang.manager.pojo.vo.req.CouponConfigUpdateReq;
import cn.fuguang.manager.pojo.vo.res.CouponConfigOverviewRes;
import cn.fuguang.manager.pojo.vo.res.CouponConfigPageRes;
import cn.fuguang.manager.pojo.vo.res.CouponConfigRes;
import cn.fuguang.manager.service.ManagerLogService;
import cn.fuguang.web.BaseResult;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/manager/coupon")
@Slf4j
public class CouponController {

    // TODO: 2024/8/6 入参，出参应该用aop切面实现 暂时先硬编码

    @Resource
    private CouponBiz couponBiz;

    @Resource
    private ManagerLogService managerLogService;

    @PostMapping("/addCouponConfig")
    public BaseResult<Void> addCouponConfig(@RequestBody @Valid CouponConfigReq req){
        log.info("[coupon] addCouponConfig req:{}", JSONObject.toJSONString(req));
        try {
            couponBiz.addCouponConfig(req);
        } catch (Exception e) {
            log.error("[coupon] addCouponConfig exception" + e);
            return BaseResult.fail();
        }
        managerLogService.record("coupon", "addCouponConfig", req.getCouponName(), "admin", "新增优惠券配置：" + req.getCouponName());
        log.info("[coupon] addCouponConfig success");
        return BaseResult.success();
    }

    @GetMapping("/getCouponConfigDetail")
    public BaseResult<CouponConfigRes> getCouponConfigDetail(@RequestParam("couponConfigId") String couponConfigId) {
        log.info("[coupon] getCouponConfigDetail couponConfigId:{}", couponConfigId);
        try {
            return BaseResult.success(couponBiz.getCouponConfigDetail(couponConfigId));
        } catch (Exception e) {
            log.error("[coupon] getCouponConfigDetail exception", e);
            return BaseResult.fail();
        }
    }

    @PostMapping("/updateCouponConfig")
    public BaseResult<Void> updateCouponConfig(@RequestBody @Valid CouponConfigUpdateReq req) {
        log.info("[coupon] updateCouponConfig req:{}", JSONObject.toJSONString(req));
        try {
            couponBiz.updateCouponConfig(req);
        } catch (Exception e) {
            log.error("[coupon] updateCouponConfig exception", e);
            return BaseResult.fail();
        }
        managerLogService.record("coupon", "updateCouponConfig", req.getCouponConfigId(), "admin", "编辑优惠券配置：" + req.getCouponName());
        log.info("[coupon] updateCouponConfig success");
        return BaseResult.success();
    }

    @PostMapping("/changeCouponConfigStatus")
    public BaseResult<Void> changeCouponConfigStatus(@RequestBody @Valid CouponConfigStatusReq req) {
        log.info("[coupon] changeCouponConfigStatus req:{}", JSONObject.toJSONString(req));
        try {
            couponBiz.changeCouponConfigStatus(req);
        } catch (IllegalArgumentException e) {
            log.warn("[coupon] changeCouponConfigStatus invalid req:{}", JSONObject.toJSONString(req), e);
            return BaseResult.fail(e.getMessage());
        } catch (Exception e) {
            log.error("[coupon] changeCouponConfigStatus exception", e);
            return BaseResult.fail();
        }
        managerLogService.record("coupon", "changeCouponConfigStatus", req.getCouponConfigId(), "admin", "优惠券状态变更为：" + req.getStatus());
        log.info("[coupon] changeCouponConfigStatus success");
        return BaseResult.success();
    }

    @PostMapping("/queryCouponConfigPage")
    public BaseResult<CouponConfigPageRes> queryCouponConfigPage(@RequestBody @Valid CouponConfigPageReq req) {
        log.info("[coupon] queryCouponConfigPage req:{}", JSONObject.toJSONString(req));
        try {
            return BaseResult.success(couponBiz.queryCouponConfigPage(req));
        } catch (Exception e) {
            log.error("[coupon] queryCouponConfigPage exception", e);
            return BaseResult.fail();
        }
    }

    @PostMapping("/queryCouponConfigOverview")
    public BaseResult<CouponConfigOverviewRes> queryCouponConfigOverview(@RequestBody @Valid CouponConfigPageReq req) {
        log.info("[coupon] queryCouponConfigOverview req:{}", JSONObject.toJSONString(req));
        try {
            return BaseResult.success(couponBiz.queryCouponConfigOverview(req));
        } catch (Exception e) {
            log.error("[coupon] queryCouponConfigOverview exception", e);
            return BaseResult.fail();
        }
    }

}
