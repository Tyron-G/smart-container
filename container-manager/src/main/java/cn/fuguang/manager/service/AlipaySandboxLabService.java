package cn.fuguang.manager.service;

import cn.fuguang.manager.config.ManagerPaymentProperties;
import cn.fuguang.manager.pojo.vo.req.AlipaySandboxPrecreateReq;
import cn.fuguang.manager.pojo.vo.req.AlipaySandboxRefundReq;
import cn.fuguang.manager.pojo.vo.req.OrderPaymentReq;
import cn.fuguang.web.BaseResult;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AlipaySandboxLabService {

    @Resource
    private ManagerPaymentProperties managerPaymentProperties;

    @Resource
    private OrderManageService orderManageService;

    public BaseResult<Map<String, Object>> precreate(AlipaySandboxPrecreateReq req) {
        BaseResult<Map<String, Object>> guard = ensureSandboxReady();
        if (guard != null) {
            return guard;
        }
        String outTradeNo = trim(req == null ? null : req.getOutTradeNo());
        if (outTradeNo.length() == 0) {
            outTradeNo = "SC-SANDBOX-" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        }
        BigDecimal amount = amount(req == null ? null : req.getAmount());
        String subject = trim(req == null ? null : req.getSubject());
        if (subject.length() == 0) {
            subject = "smart-container sandbox payment";
        }
        try {
            AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
            request.setBizContent("{\"out_trade_no\":\"" + escape(outTradeNo) + "\",\"total_amount\":\"" + amount.toPlainString() + "\",\"subject\":\"" + escape(subject) + "\"}");
            AlipayTradePrecreateResponse response = buildClient().execute(request);
            Map<String, Object> data = new LinkedHashMap<String, Object>();
            data.put("success", response.isSuccess());
            data.put("outTradeNo", outTradeNo);
            data.put("amount", amount.toPlainString());
            data.put("subject", subject);
            data.put("qrCode", response.getQrCode());
            data.put("code", response.getCode());
            data.put("msg", response.getMsg());
            data.put("subCode", response.getSubCode());
            data.put("subMsg", response.getSubMsg());
            if (!response.isSuccess()) {
                return BaseResult.fail(firstNonBlank(response.getSubMsg(), response.getMsg()));
            }
            return BaseResult.success(data);
        } catch (AlipayApiException e) {
            return BaseResult.fail("支付宝沙箱预下单异常：" + alipayErrorMessage(e));
        }
    }

    public BaseResult<Map<String, Object>> query(String outTradeNo) {
        BaseResult<Map<String, Object>> guard = ensureSandboxReady();
        if (guard != null) {
            return guard;
        }
        String tradeNo = trim(outTradeNo);
        if (tradeNo.length() == 0) {
            return BaseResult.fail("商户订单号不能为空");
        }
        try {
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            request.setBizContent("{\"out_trade_no\":\"" + escape(tradeNo) + "\"}");
            AlipayTradeQueryResponse response = buildClient().execute(request);
            Map<String, Object> data = new LinkedHashMap<String, Object>();
            data.put("success", response.isSuccess());
            data.put("outTradeNo", response.getOutTradeNo());
            data.put("tradeNo", response.getTradeNo());
            data.put("tradeStatus", response.getTradeStatus());
            data.put("totalAmount", response.getTotalAmount());
            data.put("buyerPayAmount", response.getBuyerPayAmount());
            data.put("sendPayDate", response.getSendPayDate());
            data.put("code", response.getCode());
            data.put("msg", response.getMsg());
            data.put("subCode", response.getSubCode());
            data.put("subMsg", response.getSubMsg());
            if (!response.isSuccess()) {
                return BaseResult.fail(firstNonBlank(response.getSubMsg(), response.getMsg()));
            }
            return BaseResult.success(data);
        } catch (AlipayApiException e) {
            return BaseResult.fail("支付宝沙箱交易查询异常：" + alipayErrorMessage(e));
        }
    }

    public BaseResult<Map<String, Object>> refund(AlipaySandboxRefundReq req) {
        BaseResult<Map<String, Object>> guard = ensureSandboxReady();
        if (guard != null) {
            return guard;
        }
        OrderPaymentReq paymentReq = new OrderPaymentReq();
        paymentReq.setRequestNo("RF-ALIPAY-LAB-" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        paymentReq.setOrderNo(trim(req == null ? null : req.getOrderNo()));
        paymentReq.setAmount(amount(req == null ? null : req.getAmount()));
        paymentReq.setReason("alipay sandbox payment lab refund");
        paymentReq.setOperator("admin");
        paymentReq.setChannelType("ALIPAY");
        paymentReq.setOriginalChannelTradeNo(trim(req == null ? null : req.getAlipayTradeNo()));
        return orderManageService.refund(paymentReq);
    }

    private BaseResult<Map<String, Object>> ensureSandboxReady() {
        ManagerPaymentProperties.Alipay alipay = managerPaymentProperties.getAlipay();
        List<String> missingFields = alipay.missingRequiredFields();
        if (!alipay.isEnabled()) {
            return BaseResult.fail("支付宝沙箱未启用");
        }
        if (!"sandbox".equalsIgnoreCase(alipay.getEnvironment())) {
            return BaseResult.fail("支付验证台仅允许支付宝沙箱环境");
        }
        if (!missingFields.isEmpty() || !alipay.validEnvironment()) {
            return BaseResult.fail("支付宝沙箱配置不完整：" + missingFields);
        }
        return null;
    }

    private AlipayClient buildClient() throws AlipayApiException {
        ManagerPaymentProperties.Alipay alipay = managerPaymentProperties.getAlipay();
        AlipayConfig config = new AlipayConfig();
        config.setServerUrl(alipay.getServerUrl());
        config.setAppId(alipay.getAppId());
        config.setPrivateKey(alipay.getMerchantPrivateKey());
        config.setAlipayPublicKey(alipay.getAlipayPublicKey());
        config.setSignType(alipay.getSignType());
        config.setCharset(alipay.getCharset());
        config.setFormat(alipay.getFormat());
        return new DefaultAlipayClient(config);
    }

    private BigDecimal amount(BigDecimal value) {
        BigDecimal actual = value == null ? new BigDecimal("0.01") : value;
        return actual.setScale(2, RoundingMode.HALF_UP);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private String escape(String value) {
        return trim(value).replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String firstNonBlank(String first, String second) {
        return trim(first).length() > 0 ? first : second;
    }

    private String alipayErrorMessage(AlipayApiException e) {
        String message = trim(e == null ? null : e.getMessage());
        if (message.contains("<!DOCTYPE") || message.contains("<html") || message.contains("&lt;!DOCTYPE") || message.contains("&lt;html")) {
            return "支付宝沙箱网关返回了非 JSON 响应，请换一个新的商户订单号后重试；如果刚付款成功，不需要再次生成二维码。";
        }
        if (message.length() == 0) {
            return "支付宝沙箱网关无明确错误信息，请稍后重试。";
        }
        return limit(message, 240);
    }

    private String limit(String value, int maxLength) {
        String actual = trim(value);
        return actual.length() > maxLength ? actual.substring(0, maxLength) : actual;
    }
}
