package cn.fuguang.manager.service;

import cn.fuguang.manager.pojo.vo.req.LoginReq;
import cn.fuguang.manager.pojo.vo.req.PermissionSaveReq;
import cn.fuguang.manager.pojo.vo.req.PermissionStatusReq;
import cn.fuguang.manager.pojo.vo.req.RolePermissionUpdateReq;
import cn.fuguang.manager.pojo.vo.req.RoleSaveReq;
import cn.fuguang.manager.pojo.vo.req.RoleStatusReq;
import cn.fuguang.web.BaseResult;

import java.util.List;
import java.util.Map;

public interface AuthManageService {

    BaseResult<Map<String, Object>> login(LoginReq req);

    BaseResult<Map<String, Object>> me(String username);

    BaseResult<List<Map<String, Object>>> permissions(String roleCode);

    BaseResult<List<Map<String, Object>>> roles();

    BaseResult<Void> changeRolePermissions(RolePermissionUpdateReq req);

    BaseResult<Void> saveRole(RoleSaveReq req);

    BaseResult<Void> changeRoleStatus(RoleStatusReq req);

    BaseResult<Void> savePermission(PermissionSaveReq req);

    BaseResult<Void> changePermissionStatus(PermissionStatusReq req);

    boolean hasPermission(String username, String permissionCode);
}
