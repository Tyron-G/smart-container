const { getSession, clearSession } = require('../../utils/session')

Page({
  data: {
    session: {},
    avatarText: 'SC'
  },

  onShow() {
    const session = getSession() || {}
    this.setData({
      session,
      avatarText: session.customerId ? session.customerId.slice(-2) : 'SC'
    })
  },

  logout() {
    clearSession()
    wx.redirectTo({ url: '/pages/login/index' })
  }
})

