const { ensureSession } = require('../../utils/session')

Page({
  data: {
    session: {}
  },

  onShow() {
    const session = ensureSession()
    if (session) {
      this.setData({ session })
    }
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

