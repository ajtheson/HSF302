package com.example.demo.repository;

import com.example.demo.entity.ExamEntity;
import com.example.demo.repository.custom.ExamRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamRepository extends JpaRepository<ExamEntity, Integer>, ExamRepositoryCustom {
    List<ExamEntity> findByQuizTeacherTeacherId(int teacherId);
}
