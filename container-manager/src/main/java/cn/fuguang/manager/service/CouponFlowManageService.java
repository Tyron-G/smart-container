package cn.fuguang.manager.service;

import cn.fuguang.manager.pojo.vo.req.CouponIssueReq;
import cn.fuguang.manager.pojo.vo.req.CouponRevokeReq;
import cn.fuguang.web.BaseResult;

import java.util.Map;

public interface CouponFlowManageService {

    BaseResult<Map<String, Object>> issues(String keyword, String status, Integer pageNum, Integer pageSize);

    BaseResult<Map<String, Object>> uses(String keyword, Integer pageNum, Integer pageSize);

    BaseResult<Void> issue(CouponIssueReq req);

    BaseResult<Void> revoke(CouponRevokeReq req);
}
