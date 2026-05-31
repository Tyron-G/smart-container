const SESSION_KEY = 'smart-container-mini-session'

function getSession() {
  return wx.getStorageSync(SESSION_KEY) || null
}

function setSession(session) {
  wx.setStorageSync(SESSION_KEY, session)
}

function clearSession() {
  wx.removeStorageSync(SESSION_KEY)
}

function ensureSession() {
  const session = getSession()
  if (!session || !session.customerId) {
    wx.redirectTo({ url: '/pages/login/index' })
    return null
  }
  return session
}

module.exports = {
  getSession,
  setSession,
  clearSession,
  ensureSession
}

