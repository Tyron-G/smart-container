package cn.fuguang.manager.mapper;

import org.apache.ibatis.annotations.Param;

public interface ManagerLogMapper {

    int insertOperationLog(@Param("module") String module,
                           @Param("action") String action,
                           @Param("targetId") String targetId,
                           @Param("operator") String operator,
                           @Param("content") String content);
}
