Page({
  data: {
    customerIdText: 'CUST-10001'
  },

  demoLogin() {
    this.setData({ customerIdText: 'CUST-10001' })
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

