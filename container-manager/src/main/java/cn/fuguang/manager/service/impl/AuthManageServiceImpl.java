package cn.fuguang.manager.service.impl;

import cn.fuguang.manager.mapper.AuthManageMapper;
import cn.fuguang.manager.pojo.vo.req.LoginReq;
import cn.fuguang.manager.pojo.vo.req.PermissionSaveReq;
import cn.fuguang.manager.pojo.vo.req.PermissionStatusReq;
import cn.fuguang.manager.pojo.vo.req.RolePermissionUpdateReq;
import cn.fuguang.manager.pojo.vo.req.RoleSaveReq;
import cn.fuguang.manager.pojo.vo.req.RoleStatusReq;
import cn.fuguang.manager.service.AuthManageService;
import cn.fuguang.manager.service.ManagerLogService;
import cn.fuguang.web.BaseResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthManageServiceImpl implements AuthManageService {

    @Resource
    private AuthManageMapper authManageMapper;

    @Resource
    private ManagerLogService managerLogService;

    @Override
    public BaseResult<Map<String, Object>> login(LoginReq req) {
        String username = stringValue(req == null ? null : req.getUsername());
        Map<String, Object> user = authManageMapper.selectUserByUsername(username);
        if (user == null || user.isEmpty()) {
            return BaseResult.fail("管理员账号不存在");
        }
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("token", "local-token-" + username);
        data.put("user", user);
        data.put("permissions", authManageMapper.selectPermissionsByUsername(username));
        return BaseResult.success(data);
    }

    @Override
    public BaseResult<Map<String, Object>> me(String username) {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("user", authManageMapper.selectUserByUsername(username));
        data.put("roles", authManageMapper.selectRolesByUsername(username));
        data.put("permissions", authManageMapper.selectPermissionsByUsername(username));
        return BaseResult.success(data);
    }

    @Override
    public BaseResult<List<Map<String, Object>>> permissions(String roleCode) {
        if (roleCode != null && roleCode.trim().length() > 0) {
            return BaseResult.success(authManageMapper.selectPermissionsByRoleCode(roleCode.trim()));
        }
        return BaseResult.success(authManageMapper.selectAllPermissions());
    }

    @Override
    public BaseResult<List<Map<String, Object>>> roles() {
        List<Map<String, Object>> roles = authManageMapper.selectRoles();
        for (Map<String, Object> role : roles) {
            role.put("permissions", authManageMapper.selectPermissionCodesByRoleId(String.valueOf(role.get("role_id"))));
        }
        return BaseResult.success(roles);
    }

    @Override
    public BaseResult<Void> changeRolePermissions(RolePermissionUpdateReq req) {
        String roleId = stringValue(req == null ? null : req.getRoleId());
        if (roleId.length() == 0) {
            return BaseResult.fail("roleId 不能为空");
        }
        Map<String, Object> role = authManageMapper.selectRoleById(roleId);
        if (role == null || role.isEmpty()) {
            return BaseResult.fail("角色不存在");
        }
        List<String> permissionCodes = req == null || req.getPermissionCodes() == null ? new ArrayList<String>() : req.getPermissionCodes();
        authManageMapper.deleteRolePermissions(roleId);
        for (String permissionCode : permissionCodes) {
            Map<String, Object> permission = authManageMapper.selectActivePermissionByCode(permissionCode);
            if (permission != null && !permission.isEmpty()) {
                authManageMapper.insertRolePermission(roleId, String.valueOf(permission.get("permission_id")));
            }
        }
        managerLogService.record("system", "changeRolePermissions", roleId, stringValue(req == null ? null : req.getOperator()), "保存角色权限：" + permissionCodes.size() + " 个权限点");
        return BaseResult.success();
    }

    @Override
    public BaseResult<Void> saveRole(RoleSaveReq req) {
        String roleId = stringValue(req == null ? null : req.getRoleId());
        String roleName = stringValue(req == null ? null : req.getRoleName());
        String roleCode = stringValue(req == null ? null : req.getRoleCode());
        if (roleId.length() == 0) {
            roleId = "ROLE-" + System.currentTimeMillis();
        }
        if (roleName.length() == 0 || roleCode.length() == 0) {
            return BaseResult.fail("roleName/roleCode 不能为空");
        }
        authManageMapper.upsertRole(roleId, roleName, roleCode, stringValue(req == null ? null : req.getDescription()), defaultValue(req == null ? null : req.getStatus(), "ACTIVE"));
        managerLogService.record("system", "saveRole", roleId, stringValue(req == null ? null : req.getOperator()), "新增或编辑角色：" + roleName);
        return BaseResult.success();
    }

    @Override
    public BaseResult<Void> changeRoleStatus(RoleStatusReq req) {
        String roleId = stringValue(req == null ? null : req.getRoleId());
        String status = stringValue(req == null ? null : req.getStatus());
        if (roleId.length() == 0 || status.length() == 0) {
            return BaseResult.fail("roleId/status 不能为空");
        }
        int updated = authManageMapper.updateRoleStatus(roleId, status);
        if (updated == 0) {
            return BaseResult.fail("角色不存在");
        }
        managerLogService.record("system", "changeRoleStatus", roleId, stringValue(req == null ? null : req.getOperator()), "角色状态变更为：" + status);
        return BaseResult.success();
    }

    @Override
    public BaseResult<Void> savePermission(PermissionSaveReq req) {
        String permissionId = stringValue(req == null ? null : req.getPermissionId());
        String permissionName = stringValue(req == null ? null : req.getPermissionName());
        String permissionCode = stringValue(req == null ? null : req.getPermissionCode());
        if (permissionId.length() == 0) {
            permissionId = "PERM-" + System.currentTimeMillis();
        }
        if (permissionName.length() == 0 || permissionCode.length() == 0) {
            return BaseResult.fail("permissionName/permissionCode 不能为空");
        }
        authManageMapper.upsertPermission(permissionId, permissionName, permissionCode,
                defaultValue(req == null ? null : req.getPermissionType(), "ACTION"), stringValue(req == null ? null : req.getMenuKey()),
                stringValue(req == null ? null : req.getParentCode()), stringValue(req == null ? null : req.getDescription()),
                intValue(req == null ? null : req.getSort()), defaultValue(req == null ? null : req.getStatus(), "ACTIVE"));
        managerLogService.record("system", "savePermission", permissionId, stringValue(req == null ? null : req.getOperator()), "新增或编辑权限：" + permissionCode);
        return BaseResult.success();
    }

    @Override
    public BaseResult<Void> changePermissionStatus(PermissionStatusReq req) {
        String permissionId = stringValue(req == null ? null : req.getPermissionId());
        String status = stringValue(req == null ? null : req.getStatus());
        if (permissionId.length() == 0 || status.length() == 0) {
            return BaseResult.fail("permissionId/status 不能为空");
        }
        int updated = authManageMapper.updatePermissionStatus(permissionId, status);
        if (updated == 0) {
            return BaseResult.fail("权限不存在");
        }
        managerLogService.record("system", "changePermissionStatus", permissionId, stringValue(req == null ? null : req.getOperator()), "权限状态变更为：" + status);
        return BaseResult.success();
    }

    @Override
    public boolean hasPermission(String username, String permissionCode) {
        return authManageMapper.countUserPermission(username, permissionCode) > 0;
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private String defaultValue(Object value, String defaultValue) {
        String text = stringValue(value);
        return text.length() == 0 ? defaultValue : text;
    }

    private Integer intValue(Object value) {
        try {
            return value == null || String.valueOf(value).trim().length() == 0 ? 0 : Integer.valueOf(String.valueOf(value).trim());
        } catch (Exception e) {
            return 0;
        }
    }
}
