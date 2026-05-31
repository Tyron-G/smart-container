package cn.fuguang.manager.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AuthManageMapper {

    Map<String, Object> selectUserByUsername(@Param("username") String username);

    List<Map<String, Object>> selectRolesByUsername(@Param("username") String username);

    List<Map<String, Object>> selectPermissionsByUsername(@Param("username") String username);

    List<Map<String, Object>> selectPermissionsByRoleCode(@Param("roleCode") String roleCode);

    List<Map<String, Object>> selectAllPermissions();

    List<Map<String, Object>> selectRoles();

    List<Map<String, Object>> selectPermissionCodesByRoleId(@Param("roleId") String roleId);

    Map<String, Object> selectRoleById(@Param("roleId") String roleId);

    int deleteRolePermissions(@Param("roleId") String roleId);

    Map<String, Object> selectActivePermissionByCode(@Param("permissionCode") String permissionCode);

    int insertRolePermission(@Param("roleId") String roleId, @Param("permissionId") String permissionId);

    int upsertRole(@Param("roleId") String roleId,
                   @Param("roleName") String roleName,
                   @Param("roleCode") String roleCode,
                   @Param("description") String description,
                   @Param("status") String status);

    int updateRoleStatus(@Param("roleId") String roleId, @Param("status") String status);

    int upsertPermission(@Param("permissionId") String permissionId,
                         @Param("permissionName") String permissionName,
                         @Param("permissionCode") String permissionCode,
                         @Param("permissionType") String permissionType,
                         @Param("menuKey") String menuKey,
                         @Param("parentCode") String parentCode,
                         @Param("description") String description,
                         @Param("sort") Integer sort,
                         @Param("status") String status);

    int updatePermissionStatus(@Param("permissionId") String permissionId, @Param("status") String status);

    int countUserPermission(@Param("username") String username, @Param("permissionCode") String permissionCode);
}
