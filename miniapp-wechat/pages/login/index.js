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
    wx.redirectTo({ url: '/pages/home/index' })
  },

  handleWechatLogin() {
    wx.showLoading({ title: '授权中' })
    wx.login({
      success: async (res) => {
        try {
          const session = {
            customerId: this.data.customerId || 'CUST-10001',
            mobile: this.data.mobile || '13800010001',
            openId: `wechat-preview-${res.code || 'local'}`,
            bindStatus: true,
            accessToken: 'local-wechat-preview-token'
          }
          setSession(session)
          wx.redirectTo({ url: '/pages/home/index' })
        } catch (error) {
          wx.showToast({ title: error.message || '授权失败', icon: 'none' })
        } finally {
          wx.hideLoading()
        }
      },
      fail: () => {
        wx.hideLoading()
        wx.showToast({ title: '授权取消', icon: 'none' })
      }
    })
  }
})

