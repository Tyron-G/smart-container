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
      const data = await queryOrderDetail(this.data.orderNo)
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
  return {
    orderNo: row.orderNo || row.order_no,
    orderStatus: row.orderStatus || row.order_status,
    deviceId: row.deviceId || row.device_id,
    deviceName: row.deviceName || row.device_name,
    orderAmount: row.orderAmount || row.order_amount,
    payAmount: row.payAmount || row.pay_amount
  }
}

