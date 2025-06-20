package com.example.demo.service;

import com.example.demo.entity.AnswerEntity;

import java.util.List;

public interface AnswerService {
    public AnswerEntity saveAnswer(AnswerEntity answer);
    public List<AnswerEntity> findBySubmissionId(int submissionId);
    int getStudentID(int answerID);
    List<AnswerEntity> getForStatistic(int questionID, int examID);
    List<AnswerEntity> getAnswersOfSubmission(int submissionID);
    boolean insert(AnswerEntity t);
}
