package com.example.demo.util;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
@Component
public class PasswordEncoder {
    public String encode(String password) {
        try {
            // Sử dụng thuật toán SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            // Chuyển đổi byte[] sang dạng hex string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString(); // Mật khẩu đã mã hóa
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Lỗi mã hóa mật khẩu: " + e.getMessage(), e);
        }
    }
}
