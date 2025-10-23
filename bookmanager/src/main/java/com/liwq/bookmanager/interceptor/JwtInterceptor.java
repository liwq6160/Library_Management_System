package com.liwq.bookmanager.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liwq.bookmanager.annotation.NoAuth;
import com.liwq.bookmanager.annotation.RequireAdmin;
import com.liwq.bookmanager.common.Result;
import com.liwq.bookmanager.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT拦截器
 */
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是方法请求，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 检查是否有NoAuth注解
        NoAuth noAuth = handlerMethod.getMethodAnnotation(NoAuth.class);
        if (noAuth == null) {
            noAuth = handlerMethod.getBeanType().getAnnotation(NoAuth.class);
        }

        // 如果有NoAuth注解，直接放行
        if (noAuth != null) {
            return true;
        }

        // 获取请求头中的token
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证token
        if (!StringUtils.hasText(token) || !jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(Result.error(401, "未登录或登录已过期")));
            return false;
        }

        // 从token中获取用户信息
        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);

        // 将用户信息存入request
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);
        request.setAttribute("role", role);

        // 检查是否需要管理员权限
        RequireAdmin requireAdmin = handlerMethod.getMethodAnnotation(RequireAdmin.class);
        if (requireAdmin == null) {
            requireAdmin = handlerMethod.getBeanType().getAnnotation(RequireAdmin.class);
        }

        if (requireAdmin != null && !"admin".equals(role)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(Result.error(403, "需要管理员权限")));
            return false;
        }

        return true;
    }
}
