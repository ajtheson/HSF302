package com.example.demo.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class DBContext {
    @Bean
    public Connection getConnection() {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=QuizOnlineSystem;encrypt=false";
        String username = "sa";
        String password = "123";
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println("Error: " + ex.toString());
        }
        return null;
    }
}
