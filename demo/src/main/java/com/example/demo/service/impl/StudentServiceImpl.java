package com.example.demo.service.impl;

import com.example.demo.entity.StudentEntity;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentRepository studentRepository;
    @Override
    public Optional<StudentEntity> findByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    @Override
    public StudentEntity register(StudentEntity student) {
        return studentRepository.save(student);
    }

    @Override
    public int updatePasswordByEmail(String email, String password) {
        return studentRepository.updatePasswordByEmail(email, password);
    }

    @Override
    public boolean update(StudentEntity t, String[] params) {
        return studentRepository.update(t, params);
    }

    @Override
    public boolean changePassword(String email, String password) {
        return studentRepository.changePassword(email, password);
    }

    @Override
    public StudentEntity get(int id) {
        return studentRepository.findById(id).get();
    }
}
