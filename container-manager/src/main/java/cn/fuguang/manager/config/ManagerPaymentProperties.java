package cn.fuguang.manager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "manager.payment")
public class ManagerPaymentProperties {

    private Alipay alipay = new Alipay();

    private Wechat wechat = new Wechat();

    public Alipay getAlipay() {
        return alipay;
    }

    public void setAlipay(Alipay alipay) {
        this.alipay = alipay;
    }

    public Wechat getWechat() {
        return wechat;
    }

    public void setWechat(Wechat wechat) {
        this.wechat = wechat;
    }

    public static class Alipay {
        private boolean enabled = false;
        private String environment = "";
        private String serverUrl = "";
        private String appId = "";
        private String merchantPrivateKey = "";
        private String alipayPublicKey = "";
        private String signType = "RSA2";
        private String charset = "UTF-8";
        private String format = "json";
        private boolean allowOrderNoAsOutTradeNo = false;

        public boolean readyForRealCall() {
            return enabled && missingRequiredFields().isEmpty() && validEnvironment();
        }

        public List<String> missingRequiredFields() {
            List<String> fields = new ArrayList<String>();
            if (blank(environment)) {
                fields.add("environment");
            }
            if (blank(serverUrl)) {
                fields.add("serverUrl");
            }
            if (blank(appId)) {
                fields.add("appId");
            }
            if (blank(merchantPrivateKey)) {
                fields.add("merchantPrivateKey");
            }
            if (blank(alipayPublicKey)) {
                fields.add("alipayPublicKey");
            }
            if (blank(signType)) {
                fields.add("signType");
            }
            if (blank(charset)) {
                fields.add("charset");
            }
            if (blank(format)) {
                fields.add("format");
            }
            return fields;
        }

        public boolean validEnvironment() {
            return "sandbox".equalsIgnoreCase(environment) || "production".equalsIgnoreCase(environment);
        }

        private boolean blank(String value) {
            return value == null || value.trim().length() == 0;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getEnvironment() {
            return environment;
        }

        public void setEnvironment(String environment) {
            this.environment = environment;
        }

        public String getServerUrl() {
            return serverUrl;
        }

        public void setServerUrl(String serverUrl) {
            this.serverUrl = serverUrl;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getMerchantPrivateKey() {
            return merchantPrivateKey;
        }

        public void setMerchantPrivateKey(String merchantPrivateKey) {
            this.merchantPrivateKey = merchantPrivateKey;
        }

        public String getAlipayPublicKey() {
            return alipayPublicKey;
        }

        public void setAlipayPublicKey(String alipayPublicKey) {
            this.alipayPublicKey = alipayPublicKey;
        }

        public String getSignType() {
            return signType;
        }

        public void setSignType(String signType) {
            this.signType = signType;
        }

        public String getCharset() {
            return charset;
        }

        public void setCharset(String charset) {
            this.charset = charset;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public boolean isAllowOrderNoAsOutTradeNo() {
            return allowOrderNoAsOutTradeNo;
        }

        public void setAllowOrderNoAsOutTradeNo(boolean allowOrderNoAsOutTradeNo) {
            this.allowOrderNoAsOutTradeNo = allowOrderNoAsOutTradeNo;
        }
    }

    public static class Wechat {
        private boolean enabled = false;
        private String environment = "";
        private String mchId = "";
        private String appId = "";
        private String apiV3Key = "";
        private String merchantSerialNo = "";
        private String privateKeyPath = "";
        private String refundNotifyUrl = "";
        private boolean allowOrderNoAsOutTradeNo = false;
        private boolean localTestMode = false;

        public boolean readyForRealCall() {
            return enabled && missingRequiredFields().isEmpty() && validEnvironment();
        }

        public List<String> missingRequiredFields() {
            List<String> fields = new ArrayList<String>();
            if (blank(environment)) {
                fields.add("environment");
            }
            if (blank(mchId)) {
                fields.add("mchId");
            }
            if (blank(apiV3Key)) {
                fields.add("apiV3Key");
            }
            if (blank(merchantSerialNo)) {
                fields.add("merchantSerialNo");
            }
            if (blank(privateKeyPath)) {
                fields.add("privateKeyPath");
            }
            return fields;
        }

        public boolean validEnvironment() {
            return "production".equalsIgnoreCase(environment) || "sandbox".equalsIgnoreCase(environment);
        }

        private boolean blank(String value) {
            return value == null || value.trim().length() == 0;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getEnvironment() {
            return environment;
        }

        public void setEnvironment(String environment) {
            this.environment = environment;
        }

        public String getMchId() {
            return mchId;
        }

        public void setMchId(String mchId) {
            this.mchId = mchId;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getApiV3Key() {
            return apiV3Key;
        }

        public void setApiV3Key(String apiV3Key) {
            this.apiV3Key = apiV3Key;
        }

        public String getMerchantSerialNo() {
            return merchantSerialNo;
        }

        public void setMerchantSerialNo(String merchantSerialNo) {
            this.merchantSerialNo = merchantSerialNo;
        }

        public String getPrivateKeyPath() {
            return privateKeyPath;
        }

        public void setPrivateKeyPath(String privateKeyPath) {
            this.privateKeyPath = privateKeyPath;
        }

        public String getRefundNotifyUrl() {
            return refundNotifyUrl;
        }

        public void setRefundNotifyUrl(String refundNotifyUrl) {
            this.refundNotifyUrl = refundNotifyUrl;
        }

        public boolean isAllowOrderNoAsOutTradeNo() {
            return allowOrderNoAsOutTradeNo;
        }

        public void setAllowOrderNoAsOutTradeNo(boolean allowOrderNoAsOutTradeNo) {
            this.allowOrderNoAsOutTradeNo = allowOrderNoAsOutTradeNo;
        }

        public boolean isLocalTestMode() {
            return localTestMode;
        }

        public void setLocalTestMode(boolean localTestMode) {
            this.localTestMode = localTestMode;
        }
    }
}
