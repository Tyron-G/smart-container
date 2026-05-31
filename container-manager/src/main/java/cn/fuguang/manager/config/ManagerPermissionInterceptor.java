package cn.fuguang.manager.config;

import cn.fuguang.manager.service.AuthManageService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ManagerPermissionInterceptor implements HandlerInterceptor {

    @Resource
    private AuthManageService authManageService;

    private static final Map<String, String> PATH_PERMISSION_MAP = new LinkedHashMap<String, String>();

    static {
        PATH_PERMISSION_MAP.put("GET /api/manager/order/list", "order:list");
        PATH_PERMISSION_MAP.put("GET /api/manager/order/detail", "order:detail");
        PATH_PERMISSION_MAP.put("GET /api/manager/order/statistics", "order:list");
        PATH_PERMISSION_MAP.put("POST /api/manager/order/changeStatus", "order:changeStatus");
        PATH_PERMISSION_MAP.put("POST /api/manager/order/refund", "order:refund");
        PATH_PERMISSION_MAP.put("POST /api/manager/order/supplementCharge", "order:supplementCharge");
        PATH_PERMISSION_MAP.put("POST /api/manager/order/handleException", "order:handleException");

        PATH_PERMISSION_MAP.put("POST /api/manager/coupon/addCouponConfig", "coupon:create");
        PATH_PERMISSION_MAP.put("GET /api/manager/coupon/getCouponConfigDetail", "coupon:detail");
        PATH_PERMISSION_MAP.put("POST /api/manager/coupon/updateCouponConfig", "coupon:edit");
        PATH_PERMISSION_MAP.put("POST /api/manager/coupon/changeCouponConfigStatus", "coupon:disable");
        PATH_PERMISSION_MAP.put("POST /api/manager/coupon/queryCouponConfigPage", "coupon:list");
        PATH_PERMISSION_MAP.put("POST /api/manager/coupon/queryCouponConfigOverview", "coupon:list");
        PATH_PERMISSION_MAP.put("GET /api/manager/couponFlow/issues", "coupon:flow");
        PATH_PERMISSION_MAP.put("GET /api/manager/couponFlow/uses", "coupon:flow");
        PATH_PERMISSION_MAP.put("POST /api/manager/couponFlow/issue", "coupon:issue");
        PATH_PERMISSION_MAP.put("POST /api/manager/couponFlow/revoke", "coupon:revoke");

        PATH_PERMISSION_MAP.put("GET /api/manager/customer/list", "user:list");
        PATH_PERMISSION_MAP.put("GET /api/manager/customer/detail", "user:detail");
        PATH_PERMISSION_MAP.put("GET /api/manager/customer/agreements", "user:agreements");
        PATH_PERMISSION_MAP.put("GET /api/manager/customer/orders", "user:orders");
        PATH_PERMISSION_MAP.put("GET /api/manager/customer/statistics", "user:list");
        PATH_PERMISSION_MAP.put("POST /api/manager/customer/changeStatus", "user:disable");
        PATH_PERMISSION_MAP.put("POST /api/manager/customer/create", "user:save");
        PATH_PERMISSION_MAP.put("POST /api/manager/customer/update", "user:save");

        PATH_PERMISSION_MAP.put("GET /api/manager/device/list", "device:overview");
        PATH_PERMISSION_MAP.put("GET /api/manager/device/detail", "device:monitor");
        PATH_PERMISSION_MAP.put("GET /api/manager/device/onlineCount", "device:overview");
        PATH_PERMISSION_MAP.put("GET /api/manager/device/events", "device:monitor");
        PATH_PERMISSION_MAP.put("POST /api/manager/device/save", "device:save");
        PATH_PERMISSION_MAP.put("POST /api/manager/device/saveGate", "device:saveGate");
        PATH_PERMISSION_MAP.put("POST /api/manager/device/markEventProcessed", "device:processEvent");
        PATH_PERMISSION_MAP.put("POST /api/manager/device/openGate", "device:openGate");
        PATH_PERMISSION_MAP.put("POST /api/manager/device/restart", "device:restart");

        PATH_PERMISSION_MAP.put("GET /api/manager/auth/permissions", "system:permission");
        PATH_PERMISSION_MAP.put("GET /api/manager/auth/roles", "system:role");
        PATH_PERMISSION_MAP.put("POST /api/manager/auth/changeRolePermissions", "system:roleSave");
        PATH_PERMISSION_MAP.put("POST /api/manager/auth/saveRole", "system:roleSave");
        PATH_PERMISSION_MAP.put("POST /api/manager/auth/changeRoleStatus", "system:roleStatus");
        PATH_PERMISSION_MAP.put("POST /api/manager/auth/savePermission", "system:permissionSave");
        PATH_PERMISSION_MAP.put("POST /api/manager/auth/changePermissionStatus", "system:permissionStatus");

        PATH_PERMISSION_MAP.put("GET /api/manager/audit/logs", "system:audit");
        PATH_PERMISSION_MAP.put("GET /api/manager/audit/statistics", "system:audit");

        PATH_PERMISSION_MAP.put("GET /api/manager/payment/configStatus", "system:audit");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = stripContextPath(request);
        if ("/api/manager/auth/login".equals(path) || "/api/manager/auth/me".equals(path)) {
            return true;
        }
        String permissionCode = PATH_PERMISSION_MAP.get(request.getMethod() + " " + path);
        if (permissionCode == null) {
            return true;
        }
        String username = usernameFromToken(request.getHeader("Authorization"));
        if (username.length() == 0) {
            writeForbidden(response, "未登录或登录已过期");
            return false;
        }
        if (!authManageService.hasPermission(username, permissionCode)) {
            writeForbidden(response, "无接口权限：" + permissionCode);
            return false;
        }
        return true;
    }

    private String stripContextPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && contextPath.length() > 0 && uri.startsWith(contextPath)) {
            return uri.substring(contextPath.length());
        }
        return uri;
    }

    private String usernameFromToken(String authorization) {
        if (authorization == null) {
            return "";
        }
        String token = authorization.replace("Bearer", "").trim();
        return token.startsWith("local-token-") ? token.substring("local-token-".length()).trim() : "";
    }

    private void writeForbidden(HttpServletResponse response, String message) throws IOException {
        response.setStatus(200);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":\"403\",\"message\":\"" + message + "\",\"data\":null}");
    }
}
