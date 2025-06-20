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
public class CommonLoginFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // Chỉ áp dụng cho những đường dẫn cần bảo vệ
        if (path.equals("/submission/detail") || path.equals("/account/logout")) {

            HttpSession session = request.getSession(false);

            if (session == null || session.getAttribute("account") == null) {
                // Đặt thông báo lỗi vào session
                session = request.getSession(true); // tạo session mới nếu chưa có
                session.setAttribute("errFirstLogin", "You must login first!");
                response.sendRedirect(request.getContextPath() + "/account/login");
                return;
            }
        }

        // Nếu hợp lệ thì tiếp tục filter chain
        filterChain.doFilter(request, response);
    }
}
