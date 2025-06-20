package com.example.demo.repository.custom.impl;

import com.example.demo.entity.QuestionEntity;
import com.example.demo.repository.custom.QuestionRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class QuestionRepositoryCustomImpl implements QuestionRepositoryCustom {
    @Autowired
    private Connection connection;
    private final String DELETEQUESTIONOFQUIZ = "update [question_entity] set [is_deleted] = 1 where [quiz_id] = ?";
    private final String ADDQUESTION = "insert into [question_entity] ([content], [is_multiple_choice], [is_deleted], [quiz_id]) values (?, ?, ?, ?)";
    public boolean deleteAllQuestionOfQuiz(int quizID) {
        try {
            PreparedStatement pstm = connection.prepareStatement(DELETEQUESTIONOFQUIZ);
            pstm.setInt(1, quizID);
            pstm.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int addQuestion(QuestionEntity q) {
        try (PreparedStatement pstm = connection.prepareStatement(ADDQUESTION, Statement.RETURN_GENERATED_KEYS)) {
            pstm.setString(1, q.getContent());
            pstm.setBoolean(2, q.isMultipleChoice());
            pstm.setBoolean(3, false);
            pstm.setInt(4, q.getQuiz().getQuizId());
            if (pstm.executeUpdate() > 0) {
                try (ResultSet generatedKeys = pstm.getGeneratedKeys();) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
