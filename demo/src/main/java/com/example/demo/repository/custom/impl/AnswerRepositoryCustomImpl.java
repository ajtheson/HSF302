package com.example.demo.repository.custom.impl;

import com.example.demo.entity.AnswerEntity;
import com.example.demo.entity.QuestionEntity;
import com.example.demo.entity.SubmissionEntity;
import com.example.demo.repository.custom.AnswerRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AnswerRepositoryCustomImpl implements AnswerRepositoryCustom {
    @Autowired
    private Connection connection;
    private final String INSERT = "insert into [answer_entity] ([student_choice], [is_correct], [question_id], [submission_id]) values (?, ?, ?, ?)";
    private final String GETANSWERSOFSUBMISSION = "select * from [answer_entity] where [submission_id] = ?";
    private final String GETFORQUESTIONANDEXAM = "select a.*\n"
            + "from [answer_entity] a\n"
            + "join [submission_entity] s on a.[submission_id] = s.[submission_id]\n"
            + "where a.[question_id] = ? and s.[exam_id] = ?";
    private final String GETSTUDENTID = "select s.[student_id] as [id]\n"
            + "from [answer_entity] a\n"
            + "join [submission_entity] s on a.[submission_id] = s.[submission_id]\n"
            + "where a.[answer_id] = ?";

    public int getStudentID(int answerID){
        try{
            PreparedStatement pstm = connection.prepareStatement(GETSTUDENTID);
            pstm.setInt(1, answerID);
            ResultSet rs = pstm.executeQuery();
            while(rs.next()){
                return rs.getInt("id");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

    public List<AnswerEntity> getForStatistic(int questionID, int examID) {
        List<AnswerEntity> list = new ArrayList<>();
        try {
            PreparedStatement pstm = connection.prepareStatement(GETFORQUESTIONANDEXAM);
            pstm.setInt(1, questionID);
            pstm.setInt(2, examID);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                int answerID = rs.getInt("answer_id");
                String studentChoice = rs.getString("student_choice");
                boolean isCorrect = rs.getBoolean("is_correct");
                int submissionID = rs.getInt("submission_id");
                AnswerEntity a = new AnswerEntity();
                a.setAnswerId(answerID);
                a.setStudentChoice(studentChoice);
                a.setCorrect(isCorrect);
                QuestionEntity q = new QuestionEntity();
                q.setQuestionId(questionID);
                SubmissionEntity s = new SubmissionEntity();
                s.setSubmissionId(submissionID);
                a.setQuestion(q);
                a.setSubmission(s);
                list.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<AnswerEntity> getAnswersOfSubmission(int submissionID) {
        List<AnswerEntity> list = new ArrayList<>();
        try {
            PreparedStatement pstm = connection.prepareStatement(GETANSWERSOFSUBMISSION);
            pstm.setInt(1, submissionID);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                int answerID = rs.getInt("answer_id");
                String studentChoice = rs.getString("student_choice");
                boolean isCorrect = rs.getBoolean("is_correct");
                int questionID = rs.getInt("question_id");
                AnswerEntity a = new AnswerEntity();
                a.setAnswerId(answerID);
                a.setStudentChoice(studentChoice);
                a.setCorrect(isCorrect);
                QuestionEntity q = new QuestionEntity();
                q.setQuestionId(questionID);
                SubmissionEntity s = new SubmissionEntity();
                s.setSubmissionId(submissionID);
                a.setQuestion(q);
                a.setSubmission(s);
                list.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    @Override
    public boolean insert(AnswerEntity t) {
        try {
            PreparedStatement pstm = connection.prepareStatement(INSERT);
            pstm.setString(1, t.getStudentChoice());
            pstm.setBoolean(2, t.isCorrect());
            pstm.setInt(3, t.getQuestion().getQuestionId());
            pstm.setInt(4, t.getSubmission().getSubmissionId());
            pstm.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
