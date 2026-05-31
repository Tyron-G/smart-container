const { queryOrders } = require('../../services/api')
const { ensureSession } = require('../../utils/session')

Page({
  data: {
    session: {},
    orders: [],
    loading: false
  },

  onShow() {
    const session = ensureSession()
    if (session) {
      this.setData({ session })
      this.loadOrders()
    }
  },

  async loadOrders() {
    const session = ensureSession()
    if (!session) {
      return
    }
    this.setData({ loading: true })
    try {
      const page = await queryOrders(session.customerId)
      const records = (page.records || []).map(mapOrder)
      this.setData({ orders: records })
    } catch (error) {
      wx.showToast({ title: error.message || '查询订单失败', icon: 'none' })
    } finally {
      this.setData({ loading: false })
    }
  },

  goDetail(event) {
    const orderNo = event.currentTarget.dataset.orderNo
    wx.navigateTo({ url: `/pages/orders/detail?orderNo=${orderNo}` })
  }
})

function mapOrder(row) {
  return {
    orderNo: row.orderNo || row.order_no,
    orderStatus: row.orderStatus || row.order_status,
    deviceId: row.deviceId || row.device_id,
    orderAmount: row.orderAmount || row.order_amount,
    payAmount: row.payAmount || row.pay_amount,
    createTime: row.createTime || row.create_time,
    trxTime: row.trxTime || row.trx_time
  }
}

