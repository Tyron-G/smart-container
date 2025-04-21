package cn.fuguang.order.pojo;

import cn.fuguang.entity.CustomerEntity;
import cn.fuguang.order.pojo.vo.req.ScanCreateOrderReq;
import lombok.Data;

@Data
public class OrderContext {

    private String orderNo;

    private CustomerEntity customer;

    private ScanCreateOrderReq req;
}
