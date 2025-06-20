package com.example.demo.repository.custom;

import com.example.demo.entity.TeacherEntity;

public interface TeacherRepositoryCustom {
    boolean update(TeacherEntity t, String[] params);
    boolean changePassword(String email, String password);
}
