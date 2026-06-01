const { getSession } = require('./utils/session')

App({
  globalData: {
    apiBaseUrl: 'http://localhost:9960/order'
  },
  onLaunch() {
    const session = getSession()
    this.globalData.session = session
  }
})

