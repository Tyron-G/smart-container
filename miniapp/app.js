const { getSession } = require('./utils/session')

App({
  globalData: {
    apiBaseUrl: 'http://localhost:9965/manager'
  },
  onLaunch() {
    const session = getSession()
    this.globalData.session = session
  }
})
