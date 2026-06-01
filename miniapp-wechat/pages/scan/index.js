const { queryOrderStatus, scanCreateOrder } = require('../../services/api')
const { ensureSession } = require('../../utils/session')

let statusTimer = null

Page({
  data: {
    deviceId: 'DEV-002',
    gateId: 'GATE-002-01',
    orderNo: '',
    orderStatus: '',
    orderStatusText: '',
    statusLoading: false,
    pollTimes: 0,
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

  onUnload() {
    stopStatusPolling()
  },

  onHide() {
    stopStatusPolling()
  },

  handleDeviceInput(event) {
    this.setData({ deviceId: event.detail.value })
  },

  handleGateInput(event) {
    this.setData({ gateId: event.detail.value })
  },

  scanCode() {
    wx.scanCode({
      success: (res) => {
        const parsed = parseDeviceCode(res.code || res.result)
        this.setData(parsed)
      },
      fail: () => {
        wx.showToast({ title: '扫码取消', icon: 'none' })
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
        sourceType: 'WECHAT',
        customerId: session.customerId,
        deviceId: this.data.deviceId,
        gateId: this.data.gateId
      })
      this.setData({
        orderNo: data.orderNo || '',
        orderStatus: 'INIT',
        orderStatusText: statusText('INIT'),
        pollTimes: 0
      })
      wx.showToast({ title: '订单已创建', icon: 'success' })
      this.refreshOrderStatus(true)
    } catch (error) {
      wx.showToast({ title: error.message || '创建订单失败', icon: 'none' })
    } finally {
      this.setData({ creating: false })
    }
  },

  async refreshOrderStatus(shouldPoll) {
    const session = ensureSession()
    if (!session || !this.data.orderNo) {
      return
    }
    this.setData({ statusLoading: true })
    try {
      const data = await queryOrderStatus(this.data.orderNo, session.customerId)
      const status = data.orderStatus || data.order_status || ''
      this.setData({
        orderStatus: status,
        orderStatusText: statusText(status)
      })
      if (shouldPoll && !isFinalStatus(status) && this.data.pollTimes < 5) {
        this.setData({ pollTimes: this.data.pollTimes + 1 })
        stopStatusPolling()
        statusTimer = setTimeout(() => {
          this.refreshOrderStatus(true)
        }, 3000)
      }
    } catch (error) {
      wx.showToast({ title: error.message || '查询状态失败', icon: 'none' })
    } finally {
      this.setData({ statusLoading: false })
    }
  },

  manualRefreshOrderStatus() {
    stopStatusPolling()
    this.refreshOrderStatus(false)
  },

  goDetail() {
    wx.navigateTo({ url: `/pages/orders/detail?orderNo=${this.data.orderNo}` })
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

function stopStatusPolling() {
  if (statusTimer) {
    clearTimeout(statusTimer)
    statusTimer = null
  }
}

function statusText(status) {
  const map = {
    INIT: '待确认',
    PRE_AUTH_CREATE_SUCCESS: '预授权中',
    PRE_AUTH_SUCCESS: '已开柜',
    FULLY_PAY: '已完成',
    CANCEL: '已取消',
    EXCEPTION: '异常'
  }
  return map[status] || status || '-'
}

function isFinalStatus(status) {
  return ['FULLY_PAY', 'CANCEL', 'EXCEPTION'].indexOf(status) >= 0
}

