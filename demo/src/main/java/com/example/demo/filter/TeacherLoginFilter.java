package com.example.demo.filter;

import com.example.demo.entity.TeacherEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TeacherLoginFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // Danh sách URL cần lọc
        boolean match = path.equals("/setting/teacher") ||
                path.equals("/exam/on_going") ||
                path.equals("/exam/result") ||
                path.equals("/exam/create") ||
                path.equals("/exam/completed") ||
                path.equals("/teacher_dashboard") ||
                path.startsWith("/quiz/");

        if (match) {
            HttpSession session = request.getSession(false);
            if (session == null || !(session.getAttribute("account") instanceof TeacherEntity)) {
                if (session != null) {
                    session.setAttribute("errFirstLogin", "You must login first!");
                }
                response.sendRedirect(request.getContextPath() + "/account/login");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
