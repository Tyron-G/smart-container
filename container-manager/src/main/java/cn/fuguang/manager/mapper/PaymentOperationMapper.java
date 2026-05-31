package cn.fuguang.manager.mapper;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentOperationMapper {

    Map<String, Object> selectByRequestNo(@Param("requestNo") String requestNo);

    int insertPaymentOperation(@Param("requestNo") String requestNo,
                               @Param("orderNo") String orderNo,
                               @Param("operationType") String operationType,
                               @Param("channelType") String channelType,
                               @Param("amount") BigDecimal amount,
                               @Param("reason") String reason,
                               @Param("status") String status,
                               @Param("channelTradeNo") String channelTradeNo,
                               @Param("channelMessage") String channelMessage,
                               @Param("errorCode") String errorCode,
                               @Param("channelRequestPayload") String channelRequestPayload,
                               @Param("channelResponsePayload") String channelResponsePayload,
                               @Param("operator") String operator);
}
