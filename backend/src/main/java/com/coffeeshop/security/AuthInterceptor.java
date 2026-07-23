package com.coffeeshop.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // If it's not a method handler (e.g., static resource), let it pass
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);

        // If the method doesn't have the annotation, check the class
        if (requireRole == null) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }

        // If the endpoint is not protected, allow it
        if (requireRole == null) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return false;
        }

        String token = authHeader.substring(7);
        try {
            if (jwtUtil.isTokenExpired(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token is expired");
                return false;
            }

            String userRole = jwtUtil.extractRole(token);
            String[] allowedRoles = requireRole.value();

            boolean isAllowed = Arrays.asList(allowedRoles).contains(userRole);
            if (!isAllowed) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Access Denied: Insufficient role");
                return false;
            }

            // Optional: Store email/role in request attributes for controllers to use
            request.setAttribute("userEmail", jwtUtil.extractEmail(token));
            request.setAttribute("userRole", userRole);

            return true;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid Token");
            return false;
        }
    }
}
