package com.example.demo.filter;

import com.example.demo.entity.StudentEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class StudentLoginFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // Chỉ lọc những URL mong muốn
        if (path.equals("/submission/result")
                || path.equals("/submission/list")
                || path.equals("/setting/student")
                || path.equals("/exam/take")
                || path.equals("/exam/join")) {

            HttpSession session = request.getSession(false);

            if (session == null || !(session.getAttribute("account") instanceof StudentEntity)) {
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
