package cn.fuguang.order.biz;

import cn.fuguang.order.pojo.dto.FlashSaleDto;

public interface FlashSaleBiz {

    void sale(FlashSaleDto flashSaleDto);


    void processFlashSale(FlashSaleDto flashSaleDto);


    String createSaleOrder(FlashSaleDto flashSaleDto);
}
