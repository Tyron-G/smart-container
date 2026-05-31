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
    url: '/api/manager/customer/orders',
    method: 'GET',
    data: {
      customerId,
      pageNum: 1,
      pageSize: 20
    },
    header: {
      // 2026-05-31: 本地演示复用管理端只读订单历史接口，后续替换为小程序用户端接口。
      Authorization: 'Bearer local-token-admin'
    }
  })
}

function queryOrderDetail(orderNo) {
  return request({
    url: '/api/manager/order/detail',
    method: 'GET',
    data: { orderNo },
    header: {
      // 2026-05-31: 本地演示复用管理端订单详情接口，后续替换为用户端订单详情接口。
      Authorization: 'Bearer local-token-admin'
    }
  })
}

module.exports = {
  scanCreateOrder,
  queryOrders,
  queryOrderDetail
}

