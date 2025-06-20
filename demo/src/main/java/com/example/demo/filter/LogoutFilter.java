package com.example.demo.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LogoutFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // Áp dụng logic chỉ cho các đường dẫn cần lọc
        if (path.equals("/account/register_teacher")
                || path.equals("/account/register_student")
                || path.equals("/account/forgot")
                || path.equals("/account/login")) {

            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("account") != null) {
                session.removeAttribute("account");
            }
        }

        // Tiếp tục chuỗi filter
        filterChain.doFilter(request, response);
    }
}
