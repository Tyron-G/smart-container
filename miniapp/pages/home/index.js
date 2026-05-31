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
    my.navigateTo({ url: '/pages/scan/index' })
  },

  goOrders() {
    my.navigateTo({ url: '/pages/orders/list' })
  },

  goProfile() {
    my.navigateTo({ url: '/pages/profile/index' })
  }
})
