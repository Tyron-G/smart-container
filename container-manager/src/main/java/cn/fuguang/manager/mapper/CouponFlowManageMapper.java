package cn.fuguang.manager.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CouponFlowManageMapper {

    long countIssues(@Param("keyword") String keyword, @Param("status") String status);

    List<Map<String, Object>> selectIssues(@Param("keyword") String keyword,
                                           @Param("status") String status,
                                           @Param("offset") Integer offset,
                                           @Param("pageSize") Integer pageSize);

    long countUses(@Param("keyword") String keyword);

    List<Map<String, Object>> selectUses(@Param("keyword") String keyword,
                                         @Param("offset") Integer offset,
                                         @Param("pageSize") Integer pageSize);

    Map<String, Object> selectActiveCouponConfig(@Param("couponConfigId") String couponConfigId);

    int insertIssueRecord(@Param("couponConfigId") String couponConfigId,
                          @Param("customerId") String customerId,
                          @Param("couponName") String couponName,
                          @Param("couponAmount") Object couponAmount,
                          @Param("couponValidDate") Object couponValidDate);

    int increaseCouponIssuedCount(@Param("couponConfigId") String couponConfigId);

    int revokeIssue(@Param("issueNo") String issueNo);
}
