package com.example.demo.service;

import com.example.demo.entity.AnswerEntity;
import com.example.demo.entity.SubmissionEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface SubmissionService {
    int getExamID(int submissionID);
    boolean forceSubmit(int submissionID);
    boolean checkSubmit(int submissionID);
    List<SubmissionEntity> getOnGoing(int examID);
    int createSubmission(int studentID, int examID);
    int finishSubmission(int submissionID, LocalDateTime submitTime, int duration, int selected, int correctAnswers, double score, List<AnswerEntity> answers);
    int countAttempts(int studentID, int examID);
    int submissionCount(int teacherID);
    List<SubmissionEntity> getSubmissionOfExam(int examID);
    List<SubmissionEntity> getSubmissionOfStudent(int studentID);
    SubmissionEntity get(int id);
}
