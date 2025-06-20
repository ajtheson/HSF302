package com.example.demo.service.impl;

import com.example.demo.entity.TeacherEntity;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class TeacherServiceImpl implements TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;
    @Override
    public Optional<TeacherEntity> findByEmail(String email) {
        return teacherRepository.findByEmail(email);
    }

    @Override
    public TeacherEntity register(TeacherEntity teacherEntity) {
        return teacherRepository.save(teacherEntity);
    }

    @Override
    public int updatePasswordByEmail(String email, String password) {
        return teacherRepository.updatePasswordByEmail(email, password);
    }

    @Override
    public boolean update(TeacherEntity t, String[] params) {
        return teacherRepository.update(t, params);
    }

    @Override
    public boolean changePassword(String email, String password) {
        return teacherRepository.changePassword(email, password);
    }
}
