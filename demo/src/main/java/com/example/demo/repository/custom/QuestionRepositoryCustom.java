package com.example.demo.repository.custom;

import com.example.demo.entity.QuestionEntity;
import com.example.demo.entity.QuizEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface QuestionRepositoryCustom {
    boolean deleteAllQuestionOfQuiz(int quizID);

    int addQuestion(QuestionEntity q);
}
