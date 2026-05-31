package cn.fuguang.manager.controller;

import cn.fuguang.manager.pojo.vo.req.CouponIssueReq;
import cn.fuguang.manager.pojo.vo.req.CouponRevokeReq;
import cn.fuguang.manager.service.CouponFlowManageService;
import cn.fuguang.web.BaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/manager/couponFlow")
@Slf4j
public class CouponFlowManageController {

    @Resource
    private CouponFlowManageService couponFlowManageService;

    @GetMapping("/issues")
    public BaseResult<Map<String, Object>> issues(@RequestParam(value = "keyword", required = false) String keyword,
                                                  @RequestParam(value = "status", required = false) String status,
                                                  @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                  @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        return couponFlowManageService.issues(keyword, status, pageNum, pageSize);
    }

    @GetMapping("/uses")
    public BaseResult<Map<String, Object>> uses(@RequestParam(value = "keyword", required = false) String keyword,
                                                @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        return couponFlowManageService.uses(keyword, pageNum, pageSize);
    }

    @PostMapping("/issue")
    public BaseResult<Void> issue(@RequestBody @Valid CouponIssueReq req) {
        return couponFlowManageService.issue(req);
    }

    @PostMapping("/revoke")
    public BaseResult<Void> revoke(@RequestBody @Valid CouponRevokeReq req) {
        return couponFlowManageService.revoke(req);
    }
}
