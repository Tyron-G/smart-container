package cn.fuguang.manager.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AuditLogManageMapper {

    long countLogs(@Param("keyword") String keyword, @Param("module") String module);

    List<Map<String, Object>> selectLogs(@Param("keyword") String keyword,
                                         @Param("module") String module,
                                         @Param("offset") Integer offset,
                                         @Param("pageSize") Integer pageSize);

    long countAllLogs();

    long countTodayLogs();

    List<Map<String, Object>> selectModuleTotals();
}
