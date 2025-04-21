package cn.fuguang.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FlashSaleEntity {

    private String orderNo;

    private String productId;

    private String saleId;

    private String userId;

    private String status;

    private BigDecimal price;
}
