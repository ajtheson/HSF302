package com.example.demo.repository.custom.impl;

import com.example.demo.entity.ChoiceEntity;
import com.example.demo.repository.custom.ChoiceRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class ChoiceRepositoryCustomImpl implements ChoiceRepositoryCustom {
    @Autowired
    private Connection connection;
    private final String ADDCHOICE = "insert into [choice_entity] ([choice_content], [is_correct_choice], [is_deleted], [question_id]) values (?, ?, ?, ?)";
    private final String DELETECHOICEOFQUESTION = "update [choice_entity] set [is_deleted] = 1 where [question_id] = ?";
    private final String DELETECHOICEOFQUIZ = "update [choice_entity] set [is_deleted] = 1 where [question_id] in (select [question_id] from [question_entity] where [quiz_id] = ?)";

    @Override
    public boolean addChoice(ChoiceEntity c) {
        try {
            PreparedStatement pstm = connection.prepareStatement(ADDCHOICE);
            pstm.setString(1, c.getChoiceContent());
            pstm.setBoolean(2, c.isCorrectChoice());
            pstm.setBoolean(3, false);
            pstm.setInt(4, c.getQuestion().getQuestionId());
            pstm.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteAllChoiceOfQuestion(int questionID) {
        try {
            PreparedStatement pstm = connection.prepareStatement(DELETECHOICEOFQUESTION);
            pstm.setInt(1, questionID);
            pstm.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteAllChoiceOfQuiz(int quizID) {
        try {
            PreparedStatement pstm = connection.prepareStatement(DELETECHOICEOFQUIZ);
            pstm.setInt(1, quizID);
            pstm.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
