function request(options) {
  const app = getApp()
  const baseUrl = app.globalData.apiBaseUrl || ''
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${baseUrl}${options.url}`,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'content-type': 'application/json',
        ...(options.header || {})
      },
      success(res) {
        const body = res.data || {}
        if (body.code && body.code !== '000000') {
          reject(new Error(body.message || '业务处理失败'))
          return
        }
        resolve(body.data !== undefined ? body.data : body)
      },
      fail(error) {
        reject(new Error(error.errMsg || '网络请求失败'))
      }
    })
  })
}

module.exports = {
  request
}

