package cn.fuguang.manager.mapper;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OrderManageMapper {

    long countOrders(@Param("keyword") String keyword, @Param("status") String status);

    List<Map<String, Object>> selectOrders(@Param("keyword") String keyword,
                                           @Param("status") String status,
                                           @Param("offset") Integer offset,
                                           @Param("pageSize") Integer pageSize);

    Map<String, Object> selectOrderDetail(@Param("orderNo") String orderNo);

    List<Map<String, Object>> selectOrderEvents(@Param("orderNo") String orderNo);

    List<Map<String, Object>> selectOrderItems(@Param("orderNo") String orderNo);

    List<Map<String, Object>> selectOrderRefunds(@Param("orderNo") String orderNo);

    List<Map<String, Object>> selectOrderAdjustments(@Param("orderNo") String orderNo);

    List<Map<String, Object>> selectOrderExceptions(@Param("orderNo") String orderNo);

    long countAllOrders();

    long countOrdersByStatus(@Param("status") String status);

    long countUnpaidOrders();

    BigDecimal sumTodayPaidAmount();

    int updateOrderStatus(@Param("orderNo") String orderNo, @Param("status") String status);

    Map<String, Object> selectOrderPaymentInfo(@Param("orderNo") String orderNo);

    Map<String, Object> selectOrderStatus(@Param("orderNo") String orderNo);

    BigDecimal sumSuccessRefundAmount(@Param("orderNo") String orderNo);

    int insertRefundRecord(@Param("refundNo") String refundNo,
                           @Param("orderNo") String orderNo,
                           @Param("amount") BigDecimal amount,
                           @Param("reason") String reason,
                           @Param("status") String status,
                           @Param("operator") String operator,
                           @Param("paymentRequestNo") String paymentRequestNo);

    int insertAdjustRecord(@Param("adjustNo") String adjustNo,
                           @Param("orderNo") String orderNo,
                           @Param("amount") BigDecimal amount,
                           @Param("reason") String reason,
                           @Param("status") String status,
                           @Param("operator") String operator,
                           @Param("paymentRequestNo") String paymentRequestNo);

    int handleExceptionByExceptionNo(@Param("exceptionNo") String exceptionNo,
                                     @Param("result") String result,
                                     @Param("operator") String operator);

    int handleExceptionByOrderNo(@Param("orderNo") String orderNo,
                                 @Param("result") String result,
                                 @Param("operator") String operator);
}
