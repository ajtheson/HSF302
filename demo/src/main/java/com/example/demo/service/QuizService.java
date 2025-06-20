package com.example.demo.service;

import com.example.demo.entity.QuizEntity;

import java.util.List;

public interface QuizService {
    List<QuizEntity> findByTeacher(int teacherId);
    int countTotalQuiz(int teacherID);
    int countValidQuiz(int teacherID);
    int countUsedQuiz(int teacherID);
    QuizEntity findById(int id);
    boolean deleteQuiz(int quizID);
    boolean updateQuiz(QuizEntity q);
    boolean createQuiz(QuizEntity q);
    List<QuizEntity> getTeacherQuiz(int teacherID);
    List<QuizEntity> searchByName(int teacherID, String name);
    QuizEntity get(int id);
}
