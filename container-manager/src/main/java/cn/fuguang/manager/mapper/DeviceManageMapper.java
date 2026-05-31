package cn.fuguang.manager.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DeviceManageMapper {

    long countDevices(@Param("keyword") String keyword, @Param("status") String status);

    List<Map<String, Object>> selectDevices(@Param("keyword") String keyword,
                                            @Param("status") String status,
                                            @Param("offset") Integer offset,
                                            @Param("pageSize") Integer pageSize);

    Map<String, Object> selectDeviceById(@Param("deviceId") String deviceId);

    List<Map<String, Object>> selectGatesByDeviceId(@Param("deviceId") String deviceId);

    long countAllDevices();

    long countDevicesByStatus(@Param("status") String status);

    long countErrorDevices();

    long countTodayAlertEvents();

    long countEvents(@Param("keyword") String keyword, @Param("status") String status, @Param("processStatus") String processStatus);

    List<Map<String, Object>> selectEvents(@Param("keyword") String keyword,
                                           @Param("status") String status,
                                           @Param("processStatus") String processStatus,
                                           @Param("offset") Integer offset,
                                           @Param("pageSize") Integer pageSize);

    int upsertDevice(@Param("deviceId") String deviceId,
                     @Param("deviceSn") String deviceSn,
                     @Param("deviceName") String deviceName,
                     @Param("deviceStatus") String deviceStatus,
                     @Param("deviceType") String deviceType,
                     @Param("province") String province,
                     @Param("city") String city,
                     @Param("county") String county,
                     @Param("community") String community,
                     @Param("address") String address,
                     @Param("remarks") String remarks);

    int upsertGate(@Param("gateId") String gateId,
                   @Param("deviceId") String deviceId,
                   @Param("deviceSn") String deviceSn,
                   @Param("gateName") String gateName,
                   @Param("gateCode") String gateCode,
                   @Param("sort") Integer sort,
                   @Param("gateStatus") String gateStatus,
                   @Param("remarks") String remarks);

    int markEventProcessed(@Param("id") String id, @Param("remark") String remark, @Param("operator") String operator);

    int insertDeviceCommandEvent(@Param("eventType") String eventType,
                                 @Param("eventTag") String eventTag,
                                 @Param("eventLevel") String eventLevel,
                                 @Param("deviceId") String deviceId,
                                 @Param("deviceSn") String deviceSn,
                                 @Param("deviceStatus") String deviceStatus,
                                 @Param("content") String content,
                                 @Param("rawPayload") String rawPayload);
}
