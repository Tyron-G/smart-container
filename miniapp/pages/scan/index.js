const { scanCreateOrder } = require('../../services/api')
const { ensureSession } = require('../../utils/session')

Page({
  data: {
    deviceId: 'DEV-002',
    gateId: 'GATE-002-01',
    orderNo: '',
    creating: false
  },

  onLoad(options) {
    ensureSession()
    if (options.deviceId || options.gateId) {
      this.setData({
        deviceId: options.deviceId || this.data.deviceId,
        gateId: options.gateId || this.data.gateId
      })
    }
  },

  handleDeviceInput(event) {
    this.setData({ deviceId: event.detail.value })
  },

  handleGateInput(event) {
    this.setData({ gateId: event.detail.value })
  },

  scanCode() {
    my.scan({
      success: (res) => {
        const parsed = parseDeviceCode(res.code || res.result)
        this.setData(parsed)
      },
      fail: () => {
        my.showToast({ content: '扫码取消', type: 'none' })
      }
    })
  },

  async createOrder() {
    const session = ensureSession()
    if (!session) {
      return
    }
    this.setData({ creating: true })
    try {
      const data = await scanCreateOrder({
        sourceType: 'ALI',
        customerId: session.customerId,
        deviceId: this.data.deviceId,
        gateId: this.data.gateId
      })
      this.setData({ orderNo: data.orderNo || '' })
      my.showToast({ content: '订单已创建', type: 'success' })
    } catch (error) {
      my.showToast({ content: error.message || '创建订单失败', type: 'none' })
    } finally {
      this.setData({ creating: false })
    }
  },

  goDetail() {
    my.navigateTo({ url: `/pages/orders/detail?orderNo=${this.data.orderNo}` })
  }
})

function parseDeviceCode(raw) {
  const text = String(raw || '')
  if (text.indexOf('?') >= 0) {
    const query = text.split('?')[1] || ''
    const params = {}
    query.split('&').forEach((pair) => {
      const parts = pair.split('=')
      params[decodeURIComponent(parts[0] || '')] = decodeURIComponent(parts[1] || '')
    })
    return {
      deviceId: params.deviceId || 'DEV-002',
      gateId: params.gateId || 'GATE-002-01'
    }
  }
  const parts = text.split('|')
  return {
    deviceId: parts[0] || 'DEV-002',
    gateId: parts[1] || 'GATE-002-01'
  }
}
