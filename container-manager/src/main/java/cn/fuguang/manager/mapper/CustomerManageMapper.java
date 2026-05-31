package cn.fuguang.manager.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CustomerManageMapper {

    long countCustomers(@Param("keyword") String keyword, @Param("status") String status);

    List<Map<String, Object>> selectCustomers(@Param("keyword") String keyword,
                                              @Param("status") String status,
                                              @Param("offset") Integer offset,
                                              @Param("pageSize") Integer pageSize);

    Map<String, Object> selectCustomerDetail(@Param("customerId") String customerId);

    List<Map<String, Object>> selectAgreements(@Param("customerId") String customerId);

    List<Map<String, Object>> selectRecentOrders(@Param("customerId") String customerId);

    long countCustomerOrders(@Param("customerId") String customerId);

    List<Map<String, Object>> selectCustomerOrders(@Param("customerId") String customerId,
                                                   @Param("offset") Integer offset,
                                                   @Param("pageSize") Integer pageSize);

    long countAllCustomers();

    long countActiveCustomers();

    long countDisabledCustomers();

    long countBlacklistCustomers();

    int updateCustomerStatus(@Param("customerId") String customerId, @Param("status") String status);

    int upsertBlackCustomer(@Param("customerId") String customerId, @Param("status") String status);

    int upsertCustomer(@Param("customerId") String customerId,
                       @Param("userName") String userName,
                       @Param("mobile") String mobile,
                       @Param("aliOpenId") String aliOpenId,
                       @Param("wechatOpenId") String wechatOpenId,
                       @Param("remarks") String remarks);

    int updateCustomer(@Param("customerId") String customerId,
                       @Param("userName") String userName,
                       @Param("mobile") String mobile,
                       @Param("aliOpenId") String aliOpenId,
                       @Param("wechatOpenId") String wechatOpenId,
                       @Param("remarks") String remarks);
}
