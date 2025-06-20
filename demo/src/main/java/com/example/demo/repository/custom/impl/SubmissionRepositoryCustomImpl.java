package com.example.demo.repository.custom.impl;

import com.example.demo.entity.AnswerEntity;
import com.example.demo.entity.ExamEntity;
import com.example.demo.entity.StudentEntity;
import com.example.demo.entity.SubmissionEntity;
import com.example.demo.repository.AnswerRepository;
import com.example.demo.repository.custom.SubmissionRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SubmissionRepositoryCustomImpl implements SubmissionRepositoryCustom {
    @Autowired
    private Connection connection;
    @Autowired
    private AnswerRepository answerRepository;
    private final String SUBMISSIONCOUNT = "select COUNT(s.[submission_id]) as [submissionCount]\n"
            + "from [teacher_entity] t\n"
            + "join [quiz_entity] q on t.[teacher_id] = q.[teacher_id]\n"
            + "join [exam_entity] e on q.[quiz_id] = e.[quiz_id]\n"
            + "join [submission_entity] s on e.[exam_id] = s.[exam_id]\n"
            + "where t.[teacher_id] = ? and s.[is_submit] = 1";

    private final String GETSUBMISSIONOFEXAM = "select * from [submission_entity] where [exam_id] = ? and [is_submit] = 1 order by [submission_id] desc";

    private final String GETSUBMISSIONOFSTUDENT = "select * from [submission_entity] where [student_id] = ? and [is_submit] = 1";

    private final String GET = "select * from [submission_entity] where [submission_id] = ?";

    private final String COUNTATTEMPTS = "select COUNT(*) as [numberOfAttempts] from [submission_entity] where [student_id] = ? and [exam_id] = ? and [is_submit] = 1";

    private final String ADDSUBMISSION = "insert into [submission_entity] ([submit_time], [duration], [selected], [correct_answers], [score], [student_id], [exam_id]) values (?, ?, ?, ?, ?, ?, ?)";

    private final String CREATE = "insert into [submission_entity] ([is_submit], [student_id], [exam_id]) values (?, ?, ?)";

    private final String FINISH = "update [submission_entity] set [submit_time] = ?, [duration] = ?, [selected] = ?, [correct_answers] = ?, [score] = ?, [is_submit] = ? where [submission_id] = ?";

    private final String GETONGOING = "select * from [submission_entity] where [exam_id] = ? order by [is_submit]";

    private final String FORCESUBMIT = "update [submission_entity] set [is_submit] = 1 where [submission_id] = ?";


    public int getExamID(int submissionID) {
        try {
            PreparedStatement pstm = connection.prepareStatement(GET);
            pstm.setInt(1, submissionID);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return rs.getInt("exam_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean forceSubmit(int submissionID) {
        try {
            PreparedStatement pstm = connection.prepareStatement(FORCESUBMIT);
            pstm.setInt(1, submissionID);
            pstm.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkSubmit(int submissionID) {
        try {
            PreparedStatement pstm = connection.prepareStatement(GET);
            pstm.setInt(1, submissionID);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                return rs.getBoolean("is_submit");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<SubmissionEntity> getOnGoing(int examID) {
        List<SubmissionEntity> list = new ArrayList<>();
        try {
            PreparedStatement pstm = connection.prepareStatement(GETONGOING);
            pstm.setInt(1, examID);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                boolean isSubmit = rs.getBoolean("is_submit");
                int submissionID = rs.getInt("submission_id");
                int studentID = rs.getInt("student_id");
                if (isSubmit) {
                    LocalDateTime submitTime = rs.getTimestamp("submit_time").toLocalDateTime();
                    int duration = rs.getInt("duration");
                    int selected = rs.getInt("selected");
                    int correctAnswers = rs.getInt("correct_answers");
                    double score = rs.getDouble("score");
                    SubmissionEntity sub = new SubmissionEntity();
                    sub.setSubmissionId(submissionID);
                    sub.setSubmitTime(submitTime);
                    sub.setDuration(duration);
                    sub.setSelected(selected);
                    sub.setCorrectAnswers(correctAnswers);
                    sub.setScore(score);
                    sub.setSubmit(isSubmit);
                    StudentEntity student = new StudentEntity();
                    student.setStudentId(studentID);
                    sub.setStudent(student);
                    ExamEntity exam = new ExamEntity();
                    exam.setExamId(examID);
                    sub.setExam(exam);
                    list.add(sub);
                } else {
                    SubmissionEntity sub = new SubmissionEntity();
                    sub.setSubmissionId(submissionID);
                    sub.setSubmitTime(null);
                    sub.setDuration(-1);
                    sub.setSelected(-1);
                    sub.setCorrectAnswers(-1);
                    sub.setScore(-1.0);
                    sub.setSubmit(isSubmit);
                    StudentEntity student = new StudentEntity();
                    student.setStudentId(studentID);
                    sub.setStudent(student);
                    ExamEntity exam = new ExamEntity();
                    exam.setExamId(examID);
                    sub.setExam(exam);
                    list.add(sub);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int createSubmission(int studentID, int examID) {
        try (PreparedStatement pstm = connection.prepareStatement(CREATE, Statement.RETURN_GENERATED_KEYS)) {
            pstm.setBoolean(1, false);
            pstm.setInt(2, studentID);
            pstm.setInt(3, examID);
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

    public int finishSubmission(int submissionID, LocalDateTime submitTime, int duration, int selected, int correctAnswers, double score, List<AnswerEntity> answers) {
        try {
            PreparedStatement pstm = connection.prepareStatement(FINISH);
            pstm.setObject(1, submitTime);
            pstm.setInt(2, duration);
            pstm.setInt(3, selected);
            pstm.setInt(4, correctAnswers);
            pstm.setDouble(5, score);
            pstm.setBoolean(6, true);
            pstm.setInt(7, submissionID);
            pstm.execute();
            for (AnswerEntity a : answers) {
                a.getSubmission().setSubmissionId(submissionID);
                answerRepository.insert(a);
            }
            return submissionID;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int countAttempts(int studentID, int examID) {
        try {
            PreparedStatement pstm = connection.prepareStatement(COUNTATTEMPTS);
            pstm.setInt(1, studentID);
            pstm.setInt(2, examID);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return rs.getInt("numberOfAttempts");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int submissionCount(int teacherID) {
        try {
            PreparedStatement pstm = connection.prepareStatement(SUBMISSIONCOUNT);
            pstm.setInt(1, teacherID);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return rs.getInt("submissionCount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<SubmissionEntity> getSubmissionOfExam(int examID) {
        List<SubmissionEntity> list = new ArrayList<>();
        try {
            PreparedStatement pstm = connection.prepareStatement(GETSUBMISSIONOFEXAM);
            pstm.setInt(1, examID);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                boolean isSubmit = rs.getBoolean("is_submit");
                int submissionID = rs.getInt("submission_id");
                int studentID = rs.getInt("student_id");
                LocalDateTime submitTime = rs.getTimestamp("submit_time").toLocalDateTime();
                int duration = rs.getInt("duration");
                int selected = rs.getInt("selected");
                int correctAnswers = rs.getInt("correct_answers");
                double score = rs.getDouble("score");
                SubmissionEntity sub = new SubmissionEntity();
                sub.setSubmissionId(submissionID);
                sub.setSubmitTime(submitTime);
                sub.setDuration(duration);
                sub.setSelected(selected);
                sub.setCorrectAnswers(correctAnswers);
                sub.setScore(score);
                sub.setSubmit(isSubmit);
                StudentEntity student = new StudentEntity();
                student.setStudentId(studentID);
                sub.setStudent(student);
                ExamEntity exam = new ExamEntity();
                exam.setExamId(examID);
                sub.setExam(exam);
                list.add(sub);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<SubmissionEntity> getSubmissionOfStudent(int studentID) {
        List<SubmissionEntity> list = new ArrayList<>();
        try {
            PreparedStatement pstm = connection.prepareStatement(GETSUBMISSIONOFSTUDENT);
            pstm.setInt(1, studentID);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                boolean isSubmit = rs.getBoolean("is_submit");
                int submissionID = rs.getInt("submission_id");
                LocalDateTime submitTime = rs.getTimestamp("submit_time").toLocalDateTime();
                int duration = rs.getInt("duration");
                int selected = rs.getInt("selected");
                int correctAnswers = rs.getInt("correct_answers");
                double score = rs.getDouble("score");
                SubmissionEntity sub = new SubmissionEntity();
                sub.setSubmissionId(submissionID);
                sub.setSubmitTime(submitTime);
                sub.setDuration(duration);
                sub.setSelected(selected);
                sub.setCorrectAnswers(correctAnswers);
                sub.setScore(score);
                sub.setSubmit(isSubmit);
                StudentEntity student = new StudentEntity();
                student.setStudentId(studentID);
                sub.setStudent(student);
                int examID = rs.getInt("exam_id");
                ExamEntity exam = new ExamEntity();
                exam.setExamId(examID);
                sub.setExam(exam);
                list.add(sub);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override

    public SubmissionEntity get(int id) {
        try {
            PreparedStatement pstm = connection.prepareStatement(GET);
            pstm.setInt(1, id);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                boolean isSubmit = rs.getBoolean("is_submit");
                int submissionID = rs.getInt("submission_id");
                LocalDateTime submitTime = rs.getTimestamp("submit_time").toLocalDateTime();
                int duration = rs.getInt("duration");
                int selected = rs.getInt("selected");
                int correctAnswers = rs.getInt("correct_answers");
                double score = rs.getDouble("score");
                SubmissionEntity sub = new SubmissionEntity();
                sub.setSubmissionId(submissionID);
                sub.setSubmitTime(submitTime);
                sub.setDuration(duration);
                sub.setSelected(selected);
                sub.setCorrectAnswers(correctAnswers);
                sub.setScore(score);
                sub.setSubmit(isSubmit);
                int studentID = rs.getInt("student_id");
                StudentEntity student = new StudentEntity();
                student.setStudentId(studentID);
                sub.setStudent(student);
                int examID = rs.getInt("exam_id");
                ExamEntity exam = new ExamEntity();
                exam.setExamId(examID);
                sub.setExam(exam);
                return sub;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
