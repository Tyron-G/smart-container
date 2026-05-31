package cn.fuguang.manager.service.impl;

import cn.fuguang.manager.mapper.OrderManageMapper;
import cn.fuguang.manager.pojo.vo.req.OrderExceptionHandleReq;
import cn.fuguang.manager.pojo.vo.req.OrderPaymentReq;
import cn.fuguang.manager.pojo.vo.req.OrderStatusReq;
import cn.fuguang.manager.service.ManagerLogService;
import cn.fuguang.manager.service.ManagerPaymentGateway;
import cn.fuguang.manager.service.OrderManageService;
import cn.fuguang.web.BaseResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class OrderManageServiceImpl implements OrderManageService {

    @Resource
    private OrderManageMapper orderManageMapper;

    @Resource
    private ManagerPaymentGateway managerPaymentGateway;

    @Resource
    private ManagerLogService managerLogService;

    @Override
    public BaseResult<Map<String, Object>> list(String keyword, String status, Integer pageNum, Integer pageSize) {
        int currentPage = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int currentSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("records", orderManageMapper.selectOrders(trim(keyword), trim(status), (currentPage - 1) * currentSize, currentSize));
        data.put("total", orderManageMapper.countOrders(trim(keyword), trim(status)));
        data.put("pageNum", currentPage);
        data.put("pageSize", currentSize);
        return BaseResult.success(data);
    }

    @Override
    public BaseResult<Map<String, Object>> detail(String orderNo) {
        Map<String, Object> order = orderManageMapper.selectOrderDetail(orderNo);
        if (order == null || order.isEmpty()) {
            return BaseResult.fail("订单不存在");
        }
        Map<String, Object> data = new LinkedHashMap<String, Object>(order);
        data.put("events", orderManageMapper.selectOrderEvents(orderNo));
        data.put("items", orderManageMapper.selectOrderItems(orderNo));
        data.put("refunds", orderManageMapper.selectOrderRefunds(orderNo));
        data.put("adjustments", orderManageMapper.selectOrderAdjustments(orderNo));
        data.put("exceptions", orderManageMapper.selectOrderExceptions(orderNo));
        return BaseResult.success(data);
    }

    @Override
    public BaseResult<Map<String, Object>> statistics() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("total", orderManageMapper.countAllOrders());
        data.put("paidTotal", orderManageMapper.countOrdersByStatus("FULLY_PAY"));
        data.put("unpaidTotal", orderManageMapper.countUnpaidOrders());
        data.put("exceptionTotal", orderManageMapper.countOrdersByStatus("EXCEPTION"));
        data.put("cancelTotal", orderManageMapper.countOrdersByStatus("CANCEL"));
        data.put("todayAmount", orderManageMapper.sumTodayPaidAmount());
        return BaseResult.success(data);
    }

    @Override
    public BaseResult<Void> changeStatus(OrderStatusReq req) {
        String orderNo = stringValue(req == null ? null : req.getOrderNo());
        String status = stringValue(req == null ? null : req.getStatus());
        if (orderNo.length() == 0 || status.length() == 0) {
            return BaseResult.fail("orderNo/status 不能为空");
        }
        if (!isManageableStatus(status)) {
            return BaseResult.fail("订单状态不支持");
        }
        int updated = orderManageMapper.updateOrderStatus(orderNo, status);
        if (updated == 0) {
            return BaseResult.fail("订单不存在");
        }
        managerLogService.record("order", "changeStatus", orderNo, stringValue(req == null ? null : req.getOperator()), "订单状态修正为：" + status);
        return BaseResult.success();
    }

    @Override
    public BaseResult<Map<String, Object>> refund(OrderPaymentReq req) {
        String orderNo = stringValue(req == null ? null : req.getOrderNo());
        BigDecimal amount = decimalValue(req == null ? null : req.getAmount());
        String reason = stringValue(req == null ? null : req.getReason());
        String operator = normalizeOperator(req == null ? null : req.getOperator());
        String requestNo = requestNo(req == null ? null : req.getRequestNo(), "RF");
        if (orderNo.length() == 0 || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return BaseResult.fail("orderNo/amount 不能为空");
        }
        if (reason.length() < 2) {
            return BaseResult.fail("退款原因不能少于 2 个字符");
        }
        ManagerPaymentGateway.PaymentResult existingPayment = managerPaymentGateway.find(requestNo);
        if (existingPayment != null) {
            return BaseResult.success(managerPaymentGateway.toMap(existingPayment));
        }
        Map<String, Object> order = orderManageMapper.selectOrderPaymentInfo(orderNo);
        if (order == null || order.isEmpty()) {
            return BaseResult.fail("订单不存在");
        }
        if (!"FULLY_PAY".equals(String.valueOf(order.get("order_status")))) {
            return BaseResult.fail("仅已支付订单支持退款");
        }
        BigDecimal payAmount = decimalValue(order.get("pay_amount"));
        BigDecimal refunded = orderManageMapper.sumSuccessRefundAmount(orderNo);
        if (refunded == null) {
            refunded = BigDecimal.ZERO;
        }
        if (refunded.add(amount).compareTo(payAmount) > 0) {
            return BaseResult.fail("退款金额超过可退金额");
        }
        ManagerPaymentGateway.PaymentResult paymentResult = managerPaymentGateway.execute(requestNo, orderNo, "REFUND", amount, payAmount, reason, operator,
                stringValue(req == null ? null : req.getChannelType()), stringValue(req == null ? null : req.getOriginalChannelTradeNo()));
        orderManageMapper.insertRefundRecord(requestNo, orderNo, amount, reason, paymentResult.getStatus(), operator, paymentResult.getRequestNo());
        managerLogService.record("order", "refund", orderNo, operator, "订单退款：" + amount + "，原因：" + reason);
        return BaseResult.success(managerPaymentGateway.toMap(paymentResult));
    }

    @Override
    public BaseResult<Map<String, Object>> supplementCharge(OrderPaymentReq req) {
        String orderNo = stringValue(req == null ? null : req.getOrderNo());
        BigDecimal amount = decimalValue(req == null ? null : req.getAmount());
        String reason = stringValue(req == null ? null : req.getReason());
        String operator = normalizeOperator(req == null ? null : req.getOperator());
        String requestNo = requestNo(req == null ? null : req.getRequestNo(), "ADJ");
        if (orderNo.length() == 0 || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return BaseResult.fail("orderNo/amount 不能为空");
        }
        if (reason.length() < 2) {
            return BaseResult.fail("补扣原因不能少于 2 个字符");
        }
        ManagerPaymentGateway.PaymentResult existingPayment = managerPaymentGateway.find(requestNo);
        if (existingPayment != null) {
            return BaseResult.success(managerPaymentGateway.toMap(existingPayment));
        }
        Map<String, Object> order = orderManageMapper.selectOrderStatus(orderNo);
        if (order == null || order.isEmpty()) {
            return BaseResult.fail("订单不存在");
        }
        if ("CANCEL".equals(String.valueOf(order.get("order_status")))) {
            return BaseResult.fail("已取消订单不支持补扣");
        }
        ManagerPaymentGateway.PaymentResult paymentResult = managerPaymentGateway.execute(requestNo, orderNo, "SUPPLEMENT_CHARGE", amount, reason, operator,
                stringValue(req == null ? null : req.getChannelType()), stringValue(req == null ? null : req.getOriginalChannelTradeNo()));
        orderManageMapper.insertAdjustRecord(requestNo, orderNo, amount, reason, paymentResult.getStatus(), operator, paymentResult.getRequestNo());
        managerLogService.record("order", "supplementCharge", orderNo, operator, "订单补扣：" + amount + "，原因：" + reason);
        return BaseResult.success(managerPaymentGateway.toMap(paymentResult));
    }

    @Override
    public BaseResult<Void> handleException(OrderExceptionHandleReq req) {
        String exceptionNo = stringValue(req == null ? null : req.getExceptionNo());
        String orderNo = stringValue(req == null ? null : req.getOrderNo());
        String result = stringValue(req == null ? null : req.getResult());
        String operator = normalizeOperator(req == null ? null : req.getOperator());
        if (exceptionNo.length() == 0 && orderNo.length() == 0) {
            return BaseResult.fail("exceptionNo/orderNo 至少填写一个");
        }
        int updated = exceptionNo.length() > 0
                ? orderManageMapper.handleExceptionByExceptionNo(exceptionNo, result, operator)
                : orderManageMapper.handleExceptionByOrderNo(orderNo, result, operator);
        if (updated == 0) {
            return BaseResult.fail("异常记录不存在或已处理");
        }
        managerLogService.record("order", "handleException", exceptionNo.length() > 0 ? exceptionNo : orderNo, operator, "异常处理结果：" + result);
        return BaseResult.success();
    }

    private boolean isManageableStatus(String status) {
        return "FULLY_PAY".equals(status) || "CANCEL".equals(status) || "EXCEPTION".equals(status);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private String normalizeOperator(Object value) {
        String operator = stringValue(value);
        return operator.length() == 0 ? "admin" : operator;
    }

    private BigDecimal decimalValue(Object value) {
        try {
            return value == null || String.valueOf(value).trim().length() == 0 ? BigDecimal.ZERO : new BigDecimal(String.valueOf(value).trim());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private String requestNo(Object requestNo, String prefix) {
        String value = stringValue(requestNo);
        return value.length() > 0 ? value : prefix + "-" + System.currentTimeMillis();
    }
}
