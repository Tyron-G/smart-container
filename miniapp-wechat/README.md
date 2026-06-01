# smart-container-miniapp-wechat

智柜体验助手微信小程序源码，当前为 2026-06-01 真实 AppID 真机调试版本。

## 功能范围

- 登录页：支持演示用户登录，并通过后端 `/customer/wechatLogin` 处理微信 `wx.login`。
- 首页：展示设备识别主视觉、扫码识别入口、体验记录入口、账户入口和登录状态。
- 扫码识别：支持扫码解析设备和仓门，并生成本地体验记录。
- 状态查看：扫码生成记录后自动查询当前状态，并支持手动刷新。
- 体验记录：调用 `container-order` 用户端接口展示记录列表、状态、设备和展示值。
- 记录详情：调用 `container-order` 用户端接口展示状态、设备信息和明细信息。
- 我的：展示当前体验用户、手机号、绑定状态、OpenID 并支持退出登录。

## 页面设计标准

- 后续页面开发沿用当前石墨黑设备感、电蓝行动色、冷灰背景和少量琥珀/珊瑚强调色。
- 页面样式写入对应 `.wxss`，不在 `.wxml` 新增大段内联样式。
- 个人备案版页面优先突出扫码识别、状态查看和学习演示，不出现经营、销售、支付等前台文案。

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
- 2026-06-01：个人备案版前台文案定位为学习演示，不对外承诺商品销售、支付交易或商业运营能力。

## 微信登录环境变量

```text
MINIAPP_WECHAT_ENABLED=false
MINIAPP_WECHAT_LOCAL_TEST_MODE=true
MINIAPP_WECHAT_APP_ID=wx0650c1e3a558ae28
MINIAPP_WECHAT_APP_SECRET=
```

