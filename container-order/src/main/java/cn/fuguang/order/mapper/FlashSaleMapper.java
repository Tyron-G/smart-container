package cn.fuguang.order.mapper;

import cn.fuguang.entity.FlashSaleEntity;

public interface FlashSaleMapper {
    void insert(FlashSaleEntity entity);

    FlashSaleEntity selectByParams(String saleId, String userId);

    void updateOrderStatus(String orderNo, String status, String orgStatus);
}
