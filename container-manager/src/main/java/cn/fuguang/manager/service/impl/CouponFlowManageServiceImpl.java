package cn.fuguang.manager.service.impl;

import cn.fuguang.manager.mapper.CouponFlowManageMapper;
import cn.fuguang.manager.pojo.vo.req.CouponIssueReq;
import cn.fuguang.manager.pojo.vo.req.CouponRevokeReq;
import cn.fuguang.manager.service.CouponFlowManageService;
import cn.fuguang.manager.service.ManagerLogService;
import cn.fuguang.web.BaseResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class CouponFlowManageServiceImpl implements CouponFlowManageService {

    @Resource
    private CouponFlowManageMapper couponFlowManageMapper;

    @Resource
    private ManagerLogService managerLogService;

    @Override
    public BaseResult<Map<String, Object>> issues(String keyword, String status, Integer pageNum, Integer pageSize) {
        int currentPage = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int currentSize = pageSize == null || pageSize < 1 ? 20 : pageSize;
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("records", couponFlowManageMapper.selectIssues(trim(keyword), trim(status), (currentPage - 1) * currentSize, currentSize));
        data.put("total", couponFlowManageMapper.countIssues(trim(keyword), trim(status)));
        data.put("pageNum", currentPage);
        data.put("pageSize", currentSize);
        return BaseResult.success(data);
    }

    @Override
    public BaseResult<Map<String, Object>> uses(String keyword, Integer pageNum, Integer pageSize) {
        int currentPage = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int currentSize = pageSize == null || pageSize < 1 ? 20 : pageSize;
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("records", couponFlowManageMapper.selectUses(trim(keyword), (currentPage - 1) * currentSize, currentSize));
        data.put("total", couponFlowManageMapper.countUses(trim(keyword)));
        data.put("pageNum", currentPage);
        data.put("pageSize", currentSize);
        return BaseResult.success(data);
    }

    @Override
    public BaseResult<Void> issue(CouponIssueReq req) {
        String couponConfigId = stringValue(req == null ? null : req.getCouponConfigId());
        String customerId = stringValue(req == null ? null : req.getCustomerId());
        if (couponConfigId.length() == 0 || customerId.length() == 0) {
            return BaseResult.fail("couponConfigId/customerId 不能为空");
        }
        Map<String, Object> coupon = couponFlowManageMapper.selectActiveCouponConfig(couponConfigId);
        if (coupon == null || coupon.isEmpty()) {
            return BaseResult.fail("优惠券配置不存在或未启用");
        }
        couponFlowManageMapper.insertIssueRecord(couponConfigId, customerId, String.valueOf(coupon.get("coupon_name")),
                coupon.get("coupon_money"), coupon.get("coupon_valid_date"));
        couponFlowManageMapper.increaseCouponIssuedCount(couponConfigId);
        managerLogService.record("coupon", "issue", couponConfigId, stringValue(req == null ? null : req.getOperator()), "发放优惠券给用户：" + customerId);
        return BaseResult.success();
    }

    @Override
    public BaseResult<Void> revoke(CouponRevokeReq req) {
        String issueNo = stringValue(req == null ? null : req.getIssueNo());
        if (issueNo.length() == 0) {
            return BaseResult.fail("issueNo 不能为空");
        }
        int updated = couponFlowManageMapper.revokeIssue(issueNo);
        if (updated == 0) {
            return BaseResult.fail("发放记录不存在或已使用");
        }
        managerLogService.record("coupon", "revoke", issueNo, stringValue(req == null ? null : req.getOperator()), "撤回优惠券");
        return BaseResult.success();
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }
}
