package com.example.demo.repository.custom;

import com.example.demo.entity.StudentEntity;

public interface StudentRepositoryCustom {
    boolean update(StudentEntity t, String[] params);
    boolean changePassword(String email, String password);
}
