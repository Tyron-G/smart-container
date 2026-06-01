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
  const status = row.orderStatus || row.order_status || ''
  return {
    orderNo: row.orderNo || row.order_no,
    orderStatus: status,
    orderStatusText: statusText(status),
    deviceId: row.deviceId || row.device_id,
    orderAmount: row.orderAmount || row.order_amount || '0.00',
    payAmount: row.payAmount || row.pay_amount || row.orderAmount || row.order_amount || '0.00',
    createTime: row.createTime || row.create_time,
    trxTime: row.trxTime || row.trx_time
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

