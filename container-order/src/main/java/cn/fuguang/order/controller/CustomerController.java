package cn.fuguang.order.controller;

import cn.fuguang.order.biz.CustomerBiz;
import cn.fuguang.order.pojo.vo.req.AliAuthReq;
import cn.fuguang.order.pojo.vo.req.WechatLoginReq;
import cn.fuguang.order.pojo.vo.res.AliAuthRes;
import cn.fuguang.order.pojo.vo.res.WechatLoginRes;
import cn.fuguang.exception.ContainerException;
import cn.fuguang.web.BaseResult;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;


@RestController
@RequestMapping("customer")
@Slf4j
public class CustomerController {


    @Resource
    private CustomerBiz customerBiz;


    /**
     * 用户支付宝认证
     */
    @PostMapping("/scanResultWithConfirm")
    public BaseResult<AliAuthRes> aliAuth(@RequestBody AliAuthReq req){
        log.info("用户支付宝认证 req:{}", JSONObject.toJSONString(req));
        AliAuthRes res;
        try {
            res = customerBiz.aliAuth(req);
        } catch (Exception e) {
            log.error("用户支付宝认证 系统异常" + e);
            return BaseResult.fail("系统异常, 请稍后重试");
        }
        log.info("用户支付宝认证结果 res:{}", JSONObject.toJSONString(res));
        return BaseResult.success(res);
    }


    @PostMapping("/bindingAliCustomer")
    public BaseResult<AliAuthRes> bindingAliCustomer(@RequestBody AliAuthReq req){
        log.info("用户支付宝认证 req:{}", JSONObject.toJSONString(req));
        AliAuthRes res;
        try {
            res = customerBiz.aliAuth(req);
        } catch (Exception e) {
            log.error("用户支付宝认证 系统异常" + e);
            return BaseResult.fail("系统异常, 请稍后重试");
        }
        log.info("用户支付宝认证结果 res:{}", JSONObject.toJSONString(res));
        return BaseResult.success(res);
    }

    @PostMapping("/wechatLogin")
    public BaseResult<WechatLoginRes> wechatLogin(@RequestBody @Valid WechatLoginReq req){
        log.info("用户微信登录 req customerId:{}", req == null ? null : req.getCustomerId());
        try {
            return BaseResult.success(customerBiz.wechatLogin(req));
        } catch (ContainerException e) {
            log.error("用户微信登录业务异常", e);
            return BaseResult.fail(e.getMessage());
        } catch (Exception e) {
            log.error("用户微信登录异常", e);
            return BaseResult.fail("微信登录失败");
        }
    }

}
