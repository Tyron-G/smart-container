package cn.fuguang.order.mapper;

import cn.fuguang.entity.FlashSaleEntity;
import org.apache.ibatis.annotations.Param;

public interface FlashSaleMapper {
    void insert(FlashSaleEntity entity);

    FlashSaleEntity selectByParams(@Param("saleId") String saleId, @Param("userId") String userId);

    void updateOrderStatus(@Param("orderNo") String orderNo, @Param("status") String status, @Param("orgStatus") String orgStatus);
}
