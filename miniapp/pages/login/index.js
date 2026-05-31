const { bindAliCustomer } = require('../../services/api')
const { setSession } = require('../../utils/session')

Page({
  data: {
    customerId: 'CUST-10001',
    mobile: '13800010001'
  },

  handleCustomerIdInput(event) {
    this.setData({ customerId: event.detail.value })
  },

  handleMobileInput(event) {
    this.setData({ mobile: event.detail.value })
  },

  handleDemoLogin() {
    const session = {
      customerId: this.data.customerId || 'CUST-10001',
      mobile: this.data.mobile || '13800010001',
      openId: 'demo-openid',
      bindStatus: true,
      accessToken: 'local-demo-token'
    }
    setSession(session)
    my.redirectTo({ url: '/pages/home/index' })
  },

  handleAliLogin() {
    my.showLoading({ content: '授权中' })
    my.getAuthCode({
      scopes: 'auth_user',
      success: async (res) => {
        try {
          const data = await bindAliCustomer(res.authCode)
          setSession(data)
          my.redirectTo({ url: '/pages/home/index' })
        } catch (error) {
          my.showToast({ content: error.message || '授权失败', type: 'none' })
        } finally {
          my.hideLoading()
        }
      },
      fail: () => {
        my.hideLoading()
        my.showToast({ content: '授权取消', type: 'none' })
      }
    })
  }
})
