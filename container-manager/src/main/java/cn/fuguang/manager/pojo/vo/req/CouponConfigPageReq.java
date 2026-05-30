package cn.fuguang.manager.pojo.vo.req;

import lombok.Data;

import java.io.Serializable;

@Data
public class CouponConfigPageReq implements Serializable {

    private static final long serialVersionUID = -1L;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String keyword;

    private String couponType;

    public Integer getOffset() {
        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        return (safePageNum - 1) * safePageSize;
    }
}
