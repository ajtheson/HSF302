package com.example.demo.service;

import com.example.demo.entity.TeacherEntity;

import java.util.Optional;

public interface TeacherService {
    public Optional<TeacherEntity> findByEmail(String email);
    TeacherEntity register(TeacherEntity teacherEntity);
    int updatePasswordByEmail(String email, String password);
    boolean update(TeacherEntity t, String[] params);
    boolean changePassword(String email, String password);
}
