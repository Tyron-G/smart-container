package cn.fuguang.order.biz.impl;

import cn.fuguang.api.channel.AliPayFeignService;
import cn.fuguang.api.channel.dto.req.OauthTokenReqDTO;
import cn.fuguang.api.channel.dto.res.OauthTokenResDTO;
import cn.fuguang.entity.CustomerEntity;
import cn.fuguang.exception.ContainerException;
import cn.fuguang.feign.BaseResponse;
import cn.fuguang.order.biz.CustomerBiz;
import cn.fuguang.order.config.MiniappWechatProperties;
import cn.fuguang.order.pojo.vo.req.AliAuthReq;
import cn.fuguang.order.pojo.vo.req.WechatLoginReq;
import cn.fuguang.order.pojo.vo.res.AliAuthRes;
import cn.fuguang.order.pojo.vo.res.WechatLoginRes;
import cn.fuguang.order.service.CustomerService;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLEncoder;

@Service
@Slf4j
public class CustomerBizImpl implements CustomerBiz {


    @Resource
    private AliPayFeignService aliPayFeignService;

    @Resource
    private CustomerService customerService;

    @Resource
    private MiniappWechatProperties miniappWechatProperties;


    @Override
    public AliAuthRes aliAuth(AliAuthReq req) {
        AliAuthRes aliAuthRes = new AliAuthRes();

        BaseResponse<OauthTokenResDTO> baseResponse = aliPayFeignService.oauthToken(OauthTokenReqDTO.builder().authCode(req.getAuthCode()).build());

        if (!baseResponse.isSuccess()){
            log.error("调用channel服务阿里用户认证失败");
            throw ContainerException.CUSTOMER_AUTH_ERROR.newInstance("用户认证失败");
        }
        OauthTokenResDTO oauthTokenResDTO = baseResponse.getData();

        CustomerEntity customerEntity;
        try {
            customerEntity = customerService.queryCustomerByAliOpenId(oauthTokenResDTO.getOpenId());
        } catch (ContainerException e) {
            //查询数据库是否有此用户 判断是否第一次登陆小程序
            if (e.getDefineCode().equals(ContainerException.DATE_NOT_EXIST_ERROR.getDefineCode())){
                log.info("用户首次进行支付宝登陆认证 openId:{}", oauthTokenResDTO.getOpenId());
                aliAuthRes.setBindStatus(false);
                aliAuthRes.setOpenId(oauthTokenResDTO.getOpenId());
                aliAuthRes.setAccessToken(oauthTokenResDTO.getAccessToke());
                return aliAuthRes;
            } else {
                throw ContainerException.DATABASE_QUERY_ERROR;
            }
        }
        aliAuthRes.setBindStatus(true);
        aliAuthRes.setOpenId(oauthTokenResDTO.getOpenId());
        aliAuthRes.setMobile(customerEntity.getMobile());
        aliAuthRes.setCustomerId(customerEntity.getCustomerId());
        aliAuthRes.setAccessToken(oauthTokenResDTO.getAccessToke());
        return aliAuthRes;
    }

    @Override
    public WechatLoginRes wechatLogin(WechatLoginReq req) {
        String openId;
        if (miniappWechatProperties.isLocalTestMode() && !miniappWechatProperties.readyForRealLogin()) {
            openId = buildLocalOpenId(req);
        } else {
            if (!miniappWechatProperties.readyForRealLogin()) {
                throw ContainerException.CUSTOMER_AUTH_ERROR.newInstance("微信登录未配置 AppID 或 AppSecret");
            }
            openId = fetchWechatOpenId(req.getCode());
        }
        return buildWechatLoginRes(openId, req);
    }

    private WechatLoginRes buildWechatLoginRes(String openId, WechatLoginReq req) {
        WechatLoginRes res = new WechatLoginRes();
        res.setOpenId(openId);
        res.setAccessToken("wechat-session-" + System.currentTimeMillis());
        CustomerEntity customerEntity;
        try {
            customerEntity = customerService.queryCustomerByWechatOpenId(openId);
        } catch (ContainerException e) {
            if (!ContainerException.DATE_NOT_EXIST_ERROR.getDefineCode().equals(e.getDefineCode())) {
                throw e;
            }
            String customerId = trim(req.getCustomerId());
            if (customerId.length() == 0) {
                res.setBindStatus(false);
                return res;
            }
            customerEntity = customerService.queryCustomerById(customerId);
            customerService.bindWechatOpenId(customerId, openId);
        }
        res.setBindStatus(true);
        res.setCustomerId(customerEntity.getCustomerId());
        res.setMobile(customerEntity.getMobile());
        return res;
    }

    private String fetchWechatOpenId(String code) {
        try {
            String url = miniappWechatProperties.getJscode2SessionUrl()
                    + "?appid=" + encode(miniappWechatProperties.getAppId())
                    + "&secret=" + encode(miniappWechatProperties.getAppSecret())
                    + "&js_code=" + encode(code)
                    + "&grant_type=authorization_code";
            String response = HttpUtil.get(url, 5000);
            JSONObject body = JSONObject.parseObject(response);
            Integer errcode = body.getInteger("errcode");
            if (errcode != null && errcode != 0) {
                throw ContainerException.CUSTOMER_AUTH_ERROR.newInstance("微信登录失败 errcode:{0}", errcode);
            }
            String openId = trim(body.getString("openid"));
            if (openId.length() == 0) {
                throw ContainerException.CUSTOMER_AUTH_ERROR.newInstance("微信登录未返回 openId");
            }
            return openId;
        } catch (ContainerException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用微信 jscode2session 异常", e);
            throw ContainerException.CUSTOMER_AUTH_ERROR.newInstance("微信登录远程调用失败");
        }
    }

    private String buildLocalOpenId(WechatLoginReq req) {
        String customerId = trim(req.getCustomerId());
        if (customerId.length() > 0) {
            return "wechat-local-" + customerId;
        }
        return "wechat-local-" + Math.abs(trim(req.getCode()).hashCode());
    }

    private String encode(String value) throws Exception {
        return URLEncoder.encode(trim(value), "UTF-8");
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
