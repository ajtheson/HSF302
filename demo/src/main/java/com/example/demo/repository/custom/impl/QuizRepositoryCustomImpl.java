package com.example.demo.repository.custom.impl;

import com.example.demo.entity.ChoiceEntity;
import com.example.demo.entity.QuestionEntity;
import com.example.demo.entity.QuizEntity;
import com.example.demo.repository.ChoiceRepository;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.repository.custom.QuizRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class QuizRepositoryCustomImpl implements QuizRepositoryCustom {
    @Autowired
    private Connection connection;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private ChoiceRepository choiceRepository;

    private final String COUNTTOTALQUIZ = "select COUNT([quiz_id]) as [numberOfQuizzes] from [quiz_entity] where [teacher_id] = ?";
    private final String COUNTVALIDQUIZ = "select COUNT ([quiz_id]) as [numberOfQuizzes] from [quiz_entity] where [is_deleted] = 0 and [teacher_id] = ?";
    private final String COUNTUSEDQUIZ = "select COUNT(distinct e.[quiz_id]) as [numberOfQuizzes] from [quiz_entity] q join [exam_entity] e on q.[quiz_id] = e.[quiz_id] where q.[teacher_id] = ?";
    private final String DELETE = "update [quiz_entity] set [is_deleted] = 1 where [quiz_id] = ?";
    private final String ADDQUIZ = "insert into [quiz_entity] ([quiz_name], [quantity], [is_deleted], [teacher_id]) values (?, ?, ?, ?)";
    private final String GETTEACHERQUIZ = "select * from [quiz_entity] where [is_deleted] = 0 and [teacher_id] = ? order by [quiz_id] desc";
    private final String SEARCHBYNAME = "select * from [quiz_entity] where [is_deleted] = 0 and [quiz_name] like ? and [teacher_id] = ? order by [quiz_id] desc";

    @Override
    public int countTotalQuiz(int teacherID) {
        try {
            PreparedStatement pstm = connection.prepareStatement(COUNTTOTALQUIZ);
            pstm.setInt(1, teacherID);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return rs.getInt("numberOfQuizzes");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int countValidQuiz(int teacherID) {
        try {
            PreparedStatement pstm = connection.prepareStatement(COUNTVALIDQUIZ);
            pstm.setInt(1, teacherID);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return rs.getInt("numberOfQuizzes");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int countUsedQuiz(int teacherID) {
        try {
            PreparedStatement pstm = connection.prepareStatement(COUNTUSEDQUIZ);
            pstm.setInt(1, teacherID);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return rs.getInt("numberOfQuizzes");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean deleteQuiz(int quizID) {
        try {
            connection.setAutoCommit(false);
            PreparedStatement pstm = connection.prepareStatement(DELETE);
            pstm.setInt(1, quizID);
            pstm.execute();
            questionRepository.deleteAllQuestionOfQuiz(quizID);
            choiceRepository.deleteAllChoiceOfQuiz(quizID);
            connection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int addQuiz(QuizEntity q) {
        try (PreparedStatement pstm = connection.prepareStatement(ADDQUIZ, Statement.RETURN_GENERATED_KEYS)) {
            pstm.setString(1, q.getQuizName());
            pstm.setInt(2, q.getQuantity());
            pstm.setBoolean(3, false);
            pstm.setInt(4, q.getTeacher().getTeacherId());
            if (pstm.executeUpdate() > 0) {
                try (ResultSet generatedKeys = pstm.getGeneratedKeys()) {
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

    @Override
    public boolean updateQuiz(QuizEntity q) {
        try {
            deleteQuiz(q.getQuizId());
            createQuiz(q);
            connection.commit();
            return true;
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean createQuiz(QuizEntity q) {
        try {
            int quizID = addQuiz(q);
            for (QuestionEntity question : q.getQuestions()) {
                question.getQuiz().setQuizId(quizID);
                int questionID = questionRepository.addQuestion(question);
                for (ChoiceEntity c : question.getChoices()) {
                    c.getQuestion().setQuestionId(questionID);
                    choiceRepository.addChoice(c);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<QuizEntity> getTeacherQuiz(int teacherID) {
        List<QuizEntity> quizzes = new ArrayList<>();
        try {
            PreparedStatement pstm = connection.prepareStatement(GETTEACHERQUIZ);
            pstm.setInt(1, teacherID);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                int quizID = rs.getInt("quiz_id");
                String quizName = rs.getString("quiz_name");
                int quantity = rs.getInt("quantity");
                QuizEntity q = new QuizEntity();
                q.setQuizId(quizID);
                q.setQuizName(quizName);
                q.setQuantity(quantity);
                q.setDeleted(false);
                quizzes.add(q);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quizzes;
    }

    public List<QuizEntity> searchByName(int teacherID, String name) {
        List<QuizEntity> quizzes = new ArrayList<>();
        try {
            PreparedStatement pstm = connection.prepareStatement(SEARCHBYNAME);
            pstm.setString(1, "%" + name + "%");
            pstm.setInt(2, teacherID);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                int quizID = rs.getInt("quiz_id");
                String quizName = rs.getString("quiz_name");
                int quantity = rs.getInt("quantity");
                QuizEntity q = new QuizEntity();
                q.setQuizId(quizID);
                q.setQuizName(quizName);
                q.setQuantity(quantity);
                q.setDeleted(false);
                quizzes.add(q);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quizzes;
    }

}
