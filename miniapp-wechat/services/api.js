const { request } = require('../utils/request')

function scanCreateOrder(payload) {
  return request({
    url: '/order/scanResultWithConfirm',
    method: 'POST',
    data: payload
  })
}

function queryOrders(customerId) {
  return request({
    url: '/order/customer/list',
    method: 'GET',
    data: {
      customerId,
      pageNum: 1,
      pageSize: 20
    }
  })
}

function queryOrderDetail(orderNo, customerId) {
  return request({
    url: '/order/customer/detail',
    method: 'GET',
    data: { orderNo, customerId }
  })
}

module.exports = {
  scanCreateOrder,
  queryOrders,
  queryOrderDetail
}

