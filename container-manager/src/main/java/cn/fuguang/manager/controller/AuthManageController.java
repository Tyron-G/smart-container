package cn.fuguang.manager.controller;

import cn.fuguang.manager.pojo.vo.req.LoginReq;
import cn.fuguang.manager.pojo.vo.req.PermissionSaveReq;
import cn.fuguang.manager.pojo.vo.req.PermissionStatusReq;
import cn.fuguang.manager.pojo.vo.req.RolePermissionUpdateReq;
import cn.fuguang.manager.pojo.vo.req.RoleSaveReq;
import cn.fuguang.manager.pojo.vo.req.RoleStatusReq;
import cn.fuguang.manager.service.AuthManageService;
import cn.fuguang.web.BaseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/manager/auth")
public class AuthManageController {

    @Resource
    private AuthManageService authManageService;

    @PostMapping("/login")
    public BaseResult<Map<String, Object>> login(@RequestBody @Valid LoginReq req) {
        return authManageService.login(req);
    }

    @GetMapping("/me")
    public BaseResult<Map<String, Object>> me(@RequestParam(value = "username", required = false, defaultValue = "admin") String username) {
        return authManageService.me(username);
    }

    @GetMapping("/permissions")
    public BaseResult<List<Map<String, Object>>> permissions(@RequestParam(value = "roleCode", required = false) String roleCode) {
        return authManageService.permissions(roleCode);
    }

    @GetMapping("/roles")
    public BaseResult<List<Map<String, Object>>> roles() {
        return authManageService.roles();
    }

    @PostMapping("/changeRolePermissions")
    public BaseResult<Void> changeRolePermissions(@RequestBody @Valid RolePermissionUpdateReq req) {
        return authManageService.changeRolePermissions(req);
    }

    @PostMapping("/saveRole")
    public BaseResult<Void> saveRole(@RequestBody @Valid RoleSaveReq req) {
        return authManageService.saveRole(req);
    }

    @PostMapping("/changeRoleStatus")
    public BaseResult<Void> changeRoleStatus(@RequestBody @Valid RoleStatusReq req) {
        return authManageService.changeRoleStatus(req);
    }

    @PostMapping("/savePermission")
    public BaseResult<Void> savePermission(@RequestBody @Valid PermissionSaveReq req) {
        return authManageService.savePermission(req);
    }

    @PostMapping("/changePermissionStatus")
    public BaseResult<Void> changePermissionStatus(@RequestBody @Valid PermissionStatusReq req) {
        return authManageService.changePermissionStatus(req);
    }
}
