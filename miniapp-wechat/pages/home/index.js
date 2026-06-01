const { getSession, setSession } = require('../../utils/session')

Page({
  data: {
    customerIdText: 'CUST-10001',
    loggedIn: false
  },

  onShow() {
    const session = getSession()
    this.setData({
      customerIdText: session && session.customerId ? session.customerId : 'CUST-10001',
      loggedIn: !!(session && session.customerId)
    })
  },

  demoLogin() {
    const session = {
      customerId: 'CUST-10001',
      mobile: '13800010001',
      openId: 'demo-openid',
      bindStatus: true,
      accessToken: 'local-demo-token'
    }
    setSession(session)
    this.setData({
      customerIdText: session.customerId,
      loggedIn: true
    })
    wx.showToast({ title: '已登录', icon: 'success' })
  },

  goLogin() {
    wx.navigateTo({ url: '/pages/login/index' })
  },

  goScan() {
    wx.navigateTo({ url: '/pages/scan/index' })
  },

  goOrders() {
    wx.navigateTo({ url: '/pages/orders/list' })
  },

  goProfile() {
    wx.navigateTo({ url: '/pages/profile/index' })
  }
})

