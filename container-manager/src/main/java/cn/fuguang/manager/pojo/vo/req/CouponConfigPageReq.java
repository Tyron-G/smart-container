package cn.fuguang.manager.pojo.vo.req;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class CouponConfigPageReq implements Serializable {

    private static final long serialVersionUID = -1L;

    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 100, message = "每页条数不能超过100")
    private Integer pageSize = 10;

    @Size(max = 64, message = "关键词长度不能超过64个字符")
    private String keyword;

    @Pattern(regexp = "^(CASH|GROUP|)$", message = "优惠券类型仅支持 CASH/GROUP")
    private String couponType;

    @Pattern(regexp = "^(ACTIVE|DISABLED|DELETED|)$", message = "优惠券状态仅支持 ACTIVE/DISABLED/DELETED")
    private String status;

    public Integer getOffset() {
        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        return (safePageNum - 1) * safePageSize;
    }
}
