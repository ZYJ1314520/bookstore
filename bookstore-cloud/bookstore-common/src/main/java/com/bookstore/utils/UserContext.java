package com.bookstore.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 用户上下文 - 从Gateway传递的请求头中获取用户信息
 */
public class UserContext {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_ROLE = "X-User-Role";

    public static Long getUserId() {
        HttpServletRequest request = getRequest();
        if (request == null) return null;
        String userId = request.getHeader(HEADER_USER_ID);
        return userId != null ? Long.parseLong(userId) : null;
    }

    public static Integer getRole() {
        HttpServletRequest request = getRequest();
        if (request == null) return null;
        String role = request.getHeader(HEADER_USER_ROLE);
        return role != null ? Integer.parseInt(role) : null;
    }

    private static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
