# smart-container-miniapp-wechat

智能货柜用户端微信小程序源码，当前为 2026-06-01 真实 AppID 真机调试版本。

## 功能范围

- 登录页：支持演示用户登录，并通过后端 `/customer/wechatLogin` 处理微信 `wx.login`。
- 首页：展示货柜主视觉、扫码开柜入口、订单入口、账户入口和登录状态。
- 扫码开柜：支持扫码解析设备和仓门，并调用后端创建订单。
- 我的订单：调用 `container-order` 用户端接口展示订单列表、状态、设备和金额。
- 订单详情：调用 `container-order` 用户端接口展示订单状态、金额、取货信息和商品明细。
- 我的：展示当前客户、手机号、绑定状态、OpenID 并支持退出登录。

## 页面设计标准

- 后续页面开发沿用当前石墨黑设备感、电蓝行动色、冷灰背景和少量琥珀/珊瑚强调色。
- 页面样式写入对应 `.wxss`，不在 `.wxml` 新增大段内联样式。
- 用户端页面优先突出主行动和关键业务信息，不做后台表单堆叠式布局。

## 本地打开

使用微信小程序开发者工具打开 `miniapp-wechat/` 目录。

当前微信小程序 AppID：

```text
wx0650c1e3a558ae28
```

当前 `app.js` 默认后端地址：

```js
apiBaseUrl: 'http://localhost:9960/order'
```

如后端端口或网关路径变化，修改 `app.js` 中的 `apiBaseUrl`。

## 当前边界

- 2026-06-01：微信授权绑定已接入后端接口；真实换取 openId 需要后端通过环境变量配置 AppSecret。
- 2026-06-01：已提交真实微信小程序 AppID；未提交 AppSecret、商户号、密钥或证书。
- 2026-06-01：扫码创建订单来源为 `WECHAT`，但真实微信支付商户资金通道仍需商户配置后启用。

## 微信登录环境变量

```text
MINIAPP_WECHAT_ENABLED=false
MINIAPP_WECHAT_LOCAL_TEST_MODE=true
MINIAPP_WECHAT_APP_ID=wx0650c1e3a558ae28
MINIAPP_WECHAT_APP_SECRET=
```

