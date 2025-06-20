package com.example.demo.repository.custom.impl;

import com.example.demo.entity.ExamEntity;
import com.example.demo.entity.QuizEntity;
import com.example.demo.repository.custom.ExamRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ExamRepositoryCustomImpl implements ExamRepositoryCustom {
    @Autowired
    private Connection connection;
    private final String COUNTTEACHEREXAMS = "select COUNT([exam_id]) as [numberOfExams] from [exam_entity] e join [quiz_entity] q on e.[quiz_id] = q.[quiz_id] where q.[teacher_id] = ?";
    private final String COUNTONGOING = "select COUNT([exam_id]) as [numberOfOnGoing] from [exam_entity] e join [quiz_entity] q on e.[quiz_id] = q.[quiz_id] where GETDATE() between e.[start_time] and e.[end_time] and q.[teacher_id] = ?";
    private final String CHECKVALIDCODE = "select * from [exam_entity] where GETDATE() > [end_time] and [exam_code] = ?";
    private final String INSERT = "insert into [exam_entity] ([exam_code], [exam_name], [duration], [start_time], [end_time], [attempts], [is_review], [quiz_id]) values (?, ?, ?, ?, ?, ?, ?, ?)";
    private final String GETONGOINGEXAMS = "select e.* from [exam_entity] e join [quiz_entity] q on e.[quiz_id] = q.[quiz_id] where q.[teacher_id] = ? and e.[end_time] > GETDATE() order by e.[exam_id] desc";
    private final String SEARCHONGOINGEXAMS = "select e.* from [exam_entity] e join [quiz_entity] q on e.[quiz_id] = q.[quiz_id] where q.[teacher_id] = ? and e.[end_time] > GETDATE() and e.[exam_name] like ? order by e.[exam_id] desc";
    private final String ENDEXAM = "update [exam_entity] set [end_time] = GETDATE() where [exam_id] = ?";
    private final String GETCOMPLETEDEXAMS = "select e.* from [exam_entity] e join [quiz_entity] q on e.[quiz_id] = q.[quiz_id] where q.[teacher_id] = ? and e.[end_time] < GETDATE() order by e.[exam_id] desc";
    private final String SEARCHCOMPLETEDEXAMS = "select e.* from [exam_entity] e join [quiz_entity] q on e.[quiz_id] = q.[quiz_id] where q.[teacher_id] = ? and e.[end_time] < GETDATE() and e.[exam_name] like ? order by e.[exam_id] desc";
    private final String GET = "select * from [exam_entity] where [exam_id] = ?";
    private final String SEARCHBYCODE = "select * from [exam_entity] where GETDATE() between [start_time] and [end_time] and [exam_code] = ?";
    private final String TOGGLEREVIEW = "update [exam_entity] set [is_review] = ? where [exam_id] = ?";


    public boolean toggleReview(int examID) {
        ExamEntity e = get(examID);
        try {
            PreparedStatement pstm = connection.prepareStatement(TOGGLEREVIEW);
            pstm.setBoolean(1, !e.isReview());
            pstm.setInt(2, examID);
            pstm.execute();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public ExamEntity searchByCode(int examCode) {
        try {
            PreparedStatement pstm = connection.prepareStatement(SEARCHBYCODE);
            pstm.setInt(1, examCode);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                int examID = rs.getInt("exam_id");
                String examName = rs.getString("exam_name");
                int duration = rs.getInt("duration");
                LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
                LocalDateTime endTime = rs.getTimestamp("end_time").toLocalDateTime();
                int attempts = rs.getInt("attempts");
                boolean isReview = rs.getBoolean("is_review");
                int quizID = rs.getInt("quiz_id");
                ExamEntity e = new ExamEntity();
                e.setExamId(examID);
                e.setExamCode(examCode);
                e.setExamName(examName);
                e.setDuration(duration);
                e.setStartTime(startTime);
                e.setEndTime(endTime);
                e.setAttempts(attempts);
                e.setReview(isReview);
                QuizEntity q = new QuizEntity();
                q.setQuizId(quizID);
                e.setQuiz(q);
                return e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int countTeacherExams(int teacherID) {
        try {
            PreparedStatement pstm = connection.prepareStatement(COUNTTEACHEREXAMS);
            pstm.setInt(1, teacherID);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return rs.getInt("numberOfExams");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countOnGoingExams(int teacherID) {
        try {
            PreparedStatement pstm = connection.prepareStatement(COUNTONGOING);
            pstm.setInt(1, teacherID);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return rs.getInt("numberOfOnGoing");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean isValidCode(int examCode) {
        try {
            PreparedStatement pstm = connection.prepareStatement(CHECKVALIDCODE);
            pstm.setInt(1, examCode);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public List<ExamEntity> getOnGoingExams(int teacherID) {
        List<ExamEntity> list = new ArrayList<>();
        try {
            PreparedStatement pstm = connection.prepareStatement(GETONGOINGEXAMS);
            pstm.setInt(1, teacherID);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                int examID = rs.getInt("exam_id");
                String examName = rs.getString("exam_name");
                int duration = rs.getInt("duration");
                LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
                LocalDateTime endTime = rs.getTimestamp("end_time").toLocalDateTime();
                int attempts = rs.getInt("attempts");
                boolean isReview = rs.getBoolean("is_review");
                int quizID = rs.getInt("quiz_id");
                int examCode = rs.getInt("exam_code");
                ExamEntity e = new ExamEntity();
                e.setExamId(examID);
                e.setExamCode(examCode);
                e.setExamName(examName);
                e.setDuration(duration);
                e.setStartTime(startTime);
                e.setEndTime(endTime);
                e.setAttempts(attempts);
                e.setReview(isReview);
                QuizEntity q = new QuizEntity();
                q.setQuizId(quizID);
                e.setQuiz(q);
                list.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ExamEntity> seachOnGoingExam(int teacherID, String nameSearch) {
        List<ExamEntity> list = new ArrayList<>();
        try {
            PreparedStatement pstm = connection.prepareStatement(SEARCHONGOINGEXAMS);
            pstm.setInt(1, teacherID);
            pstm.setString(2, "%" + nameSearch + "%");
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                int examID = rs.getInt("exam_id");
                String examName = rs.getString("exam_name");
                int duration = rs.getInt("duration");
                LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
                LocalDateTime endTime = rs.getTimestamp("end_time").toLocalDateTime();
                int attempts = rs.getInt("attempts");
                boolean isReview = rs.getBoolean("is_review");
                int quizID = rs.getInt("quiz_id");
                int examCode = rs.getInt("exam_code");
                ExamEntity e = new ExamEntity();
                e.setExamId(examID);
                e.setExamCode(examCode);
                e.setExamName(examName);
                e.setDuration(duration);
                e.setStartTime(startTime);
                e.setEndTime(endTime);
                e.setAttempts(attempts);
                e.setReview(isReview);
                QuizEntity q = new QuizEntity();
                q.setQuizId(quizID);
                e.setQuiz(q);
                list.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ExamEntity> getCompletedExams(int teacherID) {
        List<ExamEntity> list = new ArrayList<>();
        try {
            PreparedStatement pstm = connection.prepareStatement(GETCOMPLETEDEXAMS);
            pstm.setInt(1, teacherID);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                int examID = rs.getInt("exam_id");
                String examName = rs.getString("exam_name");
                int duration = rs.getInt("duration");
                LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
                LocalDateTime endTime = rs.getTimestamp("end_time").toLocalDateTime();
                int attempts = rs.getInt("attempts");
                boolean isReview = rs.getBoolean("is_review");
                int quizID = rs.getInt("quiz_id");
                int examCode = rs.getInt("exam_code");
                ExamEntity e = new ExamEntity();
                e.setExamId(examID);
                e.setExamCode(examCode);
                e.setExamName(examName);
                e.setDuration(duration);
                e.setStartTime(startTime);
                e.setEndTime(endTime);
                e.setAttempts(attempts);
                e.setReview(isReview);
                QuizEntity q = new QuizEntity();
                q.setQuizId(quizID);
                e.setQuiz(q);
                list.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ExamEntity> seachCompletedExam(int teacherID, String nameSearch) {
        List<ExamEntity> list = new ArrayList<>();
        try {
            PreparedStatement pstm = connection.prepareStatement(SEARCHCOMPLETEDEXAMS);
            pstm.setInt(1, teacherID);
            pstm.setString(2, "%" + nameSearch + "%");
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                int examID = rs.getInt("exam_id");
                String examName = rs.getString("exam_name");
                int duration = rs.getInt("duration");
                LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
                LocalDateTime endTime = rs.getTimestamp("end_time").toLocalDateTime();
                int attempts = rs.getInt("attempts");
                boolean isReview = rs.getBoolean("is_review");
                int quizID = rs.getInt("quiz_id");
                int examCode = rs.getInt("exam_code");
                ExamEntity e = new ExamEntity();
                e.setExamId(examID);
                e.setExamCode(examCode);
                e.setExamName(examName);
                e.setDuration(duration);
                e.setStartTime(startTime);
                e.setEndTime(endTime);
                e.setAttempts(attempts);
                e.setReview(isReview);
                QuizEntity q = new QuizEntity();
                q.setQuizId(quizID);
                e.setQuiz(q);
                list.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean endExam(int examID) {
        try {
            PreparedStatement pstm = connection.prepareStatement(ENDEXAM);
            pstm.setInt(1, examID);
            pstm.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ExamEntity get(int id) {
        try {
            PreparedStatement pstm = connection.prepareStatement(GET);
            pstm.setInt(1, id);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                int examID = rs.getInt("exam_id");
                String examName = rs.getString("exam_name");
                int duration = rs.getInt("duration");
                LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
                LocalDateTime endTime = rs.getTimestamp("end_time").toLocalDateTime();
                int attempts = rs.getInt("attempts");
                boolean isReview = rs.getBoolean("is_review");
                int quizID = rs.getInt("quiz_id");
                int examCode = rs.getInt("exam_code");
                ExamEntity e = new ExamEntity();
                e.setExamId(examID);
                e.setExamCode(examCode);
                e.setExamName(examName);
                e.setDuration(duration);
                e.setStartTime(startTime);
                e.setEndTime(endTime);
                e.setAttempts(attempts);
                e.setReview(isReview);
                QuizEntity q = new QuizEntity();
                q.setQuizId(quizID);
                e.setQuiz(q);
                return e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insert(ExamEntity t) {
        try {
            PreparedStatement pstm = connection.prepareStatement(INSERT);
            pstm.setInt(1, t.getExamCode());
            pstm.setString(2, t.getExamName());
            pstm.setInt(3, t.getDuration());
            pstm.setObject(4, t.getStartTime());
            pstm.setObject(5, t.getEndTime());
            pstm.setInt(6, t.getAttempts());
            pstm.setBoolean(7, t.isReview());
            pstm.setInt(8, t.getQuiz().getQuizId());
            pstm.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



}
