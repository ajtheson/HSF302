package com.example.demo.service;

import com.example.demo.entity.StudentEntity;

import java.util.Optional;

public interface StudentService {
    Optional<StudentEntity> findByEmail(String email);
    StudentEntity register(StudentEntity student);
    int updatePasswordByEmail(String email, String password);
    boolean update(StudentEntity t, String[] params);
    boolean changePassword(String email, String password);
    StudentEntity get(int id);
}
