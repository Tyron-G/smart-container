const SESSION_KEY = 'smart-container-mini-session'

function getSession() {
  return my.getStorageSync({ key: SESSION_KEY }).data || null
}

function setSession(session) {
  my.setStorageSync({ key: SESSION_KEY, data: session })
}

function clearSession() {
  my.removeStorageSync({ key: SESSION_KEY })
}

function ensureSession() {
  const session = getSession()
  if (!session || !session.customerId) {
    my.redirectTo({ url: '/pages/login/index' })
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
