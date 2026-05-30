package cn.fuguang.manager.pojo.vo.res;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CouponConfigPageRes implements Serializable {

    private static final long serialVersionUID = -1L;

    private List<CouponConfigRes> records;

    private Integer pageNum;

    private Integer pageSize;

    private Long total;
}
