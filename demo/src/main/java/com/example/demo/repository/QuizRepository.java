package com.example.demo.repository;

import com.example.demo.entity.QuizEntity;
import com.example.demo.repository.custom.QuizRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<QuizEntity, Integer>, QuizRepositoryCustom {
    List<QuizEntity> findByTeacherTeacherId(int teacherId);
    List<QuizEntity> findByTeacherTeacherIdAndQuizNameLike(int teacherId, String quizName);
}
