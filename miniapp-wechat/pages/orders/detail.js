const { queryOrderDetail } = require('../../services/api')
const { ensureSession } = require('../../utils/session')

Page({
  data: {
    orderNo: '',
    detail: {},
    items: []
  },

  onLoad(options) {
    ensureSession()
    this.setData({ orderNo: options.orderNo || '' })
    this.loadDetail()
  },

  async loadDetail() {
    if (!this.data.orderNo) {
      return
    }
    wx.showLoading({ title: '加载中' })
    try {
      const session = ensureSession()
      if (!session) {
        return
      }
      const data = await queryOrderDetail(this.data.orderNo, session.customerId)
      this.setData({
        detail: mapDetail(data.order || {}),
        items: data.items || []
      })
    } catch (error) {
      wx.showToast({ title: error.message || '查询详情失败', icon: 'none' })
    } finally {
      wx.hideLoading()
    }
  }
})

function mapDetail(row) {
  const status = row.orderStatus || row.order_status || ''
  return {
    orderNo: row.orderNo || row.order_no,
    orderStatus: status,
    orderStatusText: statusText(status),
    deviceId: row.deviceId || row.device_id,
    deviceName: row.deviceName || row.device_name,
    gateId: row.gateId || row.gate_id,
    gateName: row.gateName || row.gate_name,
    orderAmount: row.orderAmount || row.order_amount || '0.00',
    payAmount: row.payAmount || row.pay_amount || row.orderAmount || row.order_amount || '0.00',
    createTime: row.createTime || row.create_time,
    payTime: row.payTime || row.pay_time
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

