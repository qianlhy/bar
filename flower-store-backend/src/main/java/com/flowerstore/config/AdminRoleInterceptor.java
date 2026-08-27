package com.flowerstore.config;

import com.alibaba.fastjson2.JSON;
import com.flowerstore.common.Result;
import com.flowerstore.entity.Admin;
import com.flowerstore.mapper.AdminMapper;
import com.flowerstore.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 员工角色权限拦截：role=3 仅允许查看订单与录入积分相关接口
 * 仅对 admin Token 生效，不影响小程序用户请求
 */
@Component
public class AdminRoleInterceptor implements HandlerInterceptor {

    private static final Set<String> STAFF_ALLOWED_PREFIXES = new HashSet<>(Arrays.asList(
            "/order/page",
            "/order/admin",
            "/points/admin/",
            "/admin/user/page",
            "/config/all",
            "/auth/"
    ));

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token == null || token.trim().isEmpty()) {
            return true;
        }

        String userType;
        Long adminId;
        try {
            userType = jwtUtils.getUserTypeFromToken(token);
            adminId = jwtUtils.getUserIdFromToken(token);
        } catch (Exception e) {
            return true;
        }

        // 非管理端 Token 不拦截
        if (!"admin".equals(userType) || adminId == null) {
            return true;
        }

        Admin admin = adminMapper.selectById(adminId);
        if (admin == null || admin.getStatus() == null || admin.getStatus() != 1) {
            writeError(response, 403, "账号已禁用或不存在");
            return false;
        }

        // 超管 / 普通管理员全放行
        if (admin.getRole() == null || admin.getRole() == 1 || admin.getRole() == 2) {
            return true;
        }

        // 员工 role=3
        if (admin.getRole() != 3) {
            writeError(response, 403, "未知角色");
            return false;
        }

        String path = request.getRequestURI();
        if (path.startsWith("/api")) {
            path = path.substring(4);
        }
        String method = request.getMethod();

        // 积分录入 / 锁
        if (path.startsWith("/points/admin/")) {
            return true;
        }
        // 查看用户列表（录入积分时选用户）
        if ("GET".equalsIgnoreCase(method) && path.startsWith("/admin/user/page")) {
            return true;
        }
        // 查看配置（积分开关只读）
        if ("GET".equalsIgnoreCase(method) && path.equals("/config/all")) {
            return true;
        }
        // 订单列表
        if ("GET".equalsIgnoreCase(method) && path.startsWith("/order/page")) {
            return true;
        }
        // 订单统计
        if ("GET".equalsIgnoreCase(method) && path.startsWith("/order/admin")) {
            return true;
        }
        // 订单详情（只读）
        if ("GET".equalsIgnoreCase(method) && path.matches("/order/\\d+")) {
            return true;
        }
        // 登录相关
        if (path.startsWith("/auth/")) {
            return true;
        }

        writeError(response, 403, "员工账号仅可查看订单和录入积分");
        return false;
    }

    private void writeError(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(Result.error(code, message)));
    }
}
