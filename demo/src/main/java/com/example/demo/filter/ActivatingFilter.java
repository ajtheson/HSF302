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
public class ActivatingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        HttpSession session = request.getSession(false);

        // Áp dụng cho các đường dẫn cần bảo vệ
        if (path.equals("/account/resend_otp") ||
                path.equals("/account/change_password") ||
                path.equals("/account/activate")) {

            // Giả sử bạn kiểm tra session đã có user activate hoặc OTP
            if (session == null || session.getAttribute("email") == null) {
                response.sendRedirect("/account/login"); // hoặc trả lỗi 403
                return;
            }
        }

        // Cho phép tiếp tục
        filterChain.doFilter(request, response);
    }
}

