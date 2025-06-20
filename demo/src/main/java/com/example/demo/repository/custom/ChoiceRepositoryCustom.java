package com.example.demo.repository.custom;

import com.example.demo.entity.ChoiceEntity;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface ChoiceRepositoryCustom {
    public boolean addChoice(ChoiceEntity c);
    public boolean deleteAllChoiceOfQuestion(int questionID);
    public boolean deleteAllChoiceOfQuiz(int quizID);
}
