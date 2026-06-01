package cn.fuguang.order.mapper;

import cn.fuguang.entity.OrderInfoEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface OrderInfoMapper {

    /**
     * 查询商户未支付的订单
     */
    List<OrderInfoEntity> queryUnpaidOrder(@Param("customerId") String customerId);

    void insert(OrderInfoEntity orderInfo);

    OrderInfoEntity queryByOrderNo(@Param("orderNo") String orderNo);

    int updateOrderStatus(OrderInfoEntity orderInfoEntity);

    long countCustomerOrders(@Param("customerId") String customerId);

    List<Map<String, Object>> queryCustomerOrders(@Param("customerId") String customerId,
                                                  @Param("offset") Integer offset,
                                                  @Param("pageSize") Integer pageSize);

    Map<String, Object> queryCustomerOrderDetail(@Param("customerId") String customerId,
                                                 @Param("orderNo") String orderNo);

    Map<String, Object> queryCustomerOrderStatus(@Param("customerId") String customerId,
                                                 @Param("orderNo") String orderNo);

    List<Map<String, Object>> queryOrderItems(@Param("orderNo") String orderNo);
}
