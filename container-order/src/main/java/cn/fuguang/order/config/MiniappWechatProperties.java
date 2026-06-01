package cn.fuguang.order.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "miniapp.wechat")
public class MiniappWechatProperties {

    private boolean enabled = false;
    private boolean localTestMode = false;
    private String appId = "";
    private String appSecret = "";
    private String jscode2SessionUrl = "https://api.weixin.qq.com/sns/jscode2session";

    public boolean readyForRealLogin() {
        return enabled && hasText(appId) && hasText(appSecret) && hasText(jscode2SessionUrl);
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isLocalTestMode() {
        return localTestMode;
    }

    public void setLocalTestMode(boolean localTestMode) {
        this.localTestMode = localTestMode;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getJscode2SessionUrl() {
        return jscode2SessionUrl;
    }

    public void setJscode2SessionUrl(String jscode2SessionUrl) {
        this.jscode2SessionUrl = jscode2SessionUrl;
    }
}
