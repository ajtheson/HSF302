package com.example.demo.repository.custom;

import com.example.demo.entity.QuizEntity;

import java.util.List;

public interface QuizRepositoryCustom {
    int countTotalQuiz(int teacherID);

    int countValidQuiz(int teacherID);

    int countUsedQuiz(int teacherID);

    boolean deleteQuiz(int quizID);

    int addQuiz(QuizEntity q);

    boolean updateQuiz(QuizEntity q);

    boolean createQuiz(QuizEntity q);

    List<QuizEntity> getTeacherQuiz(int teacherID);

    List<QuizEntity> searchByName(int teacherID, String name);
}
