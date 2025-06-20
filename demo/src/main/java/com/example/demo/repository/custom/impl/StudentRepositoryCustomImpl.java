package com.example.demo.repository.custom.impl;

import com.example.demo.entity.StudentEntity;
import com.example.demo.repository.custom.StudentRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
@Repository
public class StudentRepositoryCustomImpl implements StudentRepositoryCustom {
    @Autowired
    private Connection connection;
    private final String UPDATE = "update [student_entity] set [full_name] = ?, [class_name] = ?, [school] = ? where [student_id] = ?";
    private final String CHANGEPASSWORD = "update [student_entity] set [password] = ? where [email] = ?";
    @Override
    public boolean update(StudentEntity t, String[] params) {
        try {
            PreparedStatement pstm = connection.prepareStatement(UPDATE);
            pstm.setString(1, params[0]);
            pstm.setString(2, params[1]);
            pstm.setString(3, params[2]);
            pstm.setInt(4, t.getStudentId());
            pstm.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public boolean changePassword(String email, String password) {
        try {
            PreparedStatement pstm = connection.prepareStatement(CHANGEPASSWORD);
            pstm.setString(1, password);
            pstm.setString(2, email);
            pstm.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
