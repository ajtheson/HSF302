package com.example.demo.repository.custom.impl;

import com.example.demo.entity.TeacherEntity;
import com.example.demo.repository.custom.TeacherRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class TeacherRepositoryCustomImpl implements TeacherRepositoryCustom {
    @Autowired
    private Connection connection;
    private final String UPDATE = "update [teacher_entity] set [full_name] = ?, [school] = ? where [teacher_id] = ?";
    private final String CHANGEPASSWORD = "update [teacher_entity] set [password] = ? where [email] = ?";
    @Override
    public boolean update(TeacherEntity t, String[] params) {
        try {
            PreparedStatement pstm = connection.prepareStatement(UPDATE);
            pstm.setString(1, params[0]);
            pstm.setString(2, params[1]);
            pstm.setInt(3, t.getTeacherId());
            pstm.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

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
