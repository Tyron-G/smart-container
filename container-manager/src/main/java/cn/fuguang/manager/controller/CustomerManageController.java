package cn.fuguang.manager.controller;

import cn.fuguang.manager.pojo.vo.req.CustomerSaveReq;
import cn.fuguang.manager.pojo.vo.req.CustomerStatusReq;
import cn.fuguang.manager.service.CustomerManageService;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/manager/customer")
@Slf4j
public class CustomerManageController {

    @Resource
    private CustomerManageService customerManageService;

    @GetMapping("/list")
    public BaseResult<Map<String, Object>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                                @RequestParam(value = "status", required = false) String status,
                                                @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return customerManageService.list(keyword, status, pageNum, pageSize);
    }

    @GetMapping("/detail")
    public BaseResult<Map<String, Object>> detail(@RequestParam("customerId") String customerId) {
        return customerManageService.detail(customerId);
    }

    @GetMapping("/agreements")
    public BaseResult<List<Map<String, Object>>> agreements(@RequestParam("customerId") String customerId) {
        return customerManageService.agreements(customerId);
    }

    @GetMapping("/orders")
    public BaseResult<Map<String, Object>> orders(@RequestParam("customerId") String customerId,
                                                  @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                  @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return customerManageService.orders(customerId, pageNum, pageSize);
    }

    @GetMapping("/statistics")
    public BaseResult<Map<String, Object>> statistics() {
        return customerManageService.statistics();
    }

    @PostMapping("/changeStatus")
    public BaseResult<Void> changeStatus(@RequestBody @Valid CustomerStatusReq req) {
        return customerManageService.changeStatus(req);
    }

    @PostMapping("/create")
    public BaseResult<Void> create(@RequestBody @Valid CustomerSaveReq req) {
        return customerManageService.create(req);
    }

    @PostMapping("/update")
    public BaseResult<Void> update(@RequestBody @Valid CustomerSaveReq req) {
        return customerManageService.update(req);
    }
}
